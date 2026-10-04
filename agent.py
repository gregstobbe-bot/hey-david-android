import os
import json
import requests

GITHUB_TOKEN = os.getenv("GITHUB_TOKEN")
OPENAI_API_KEY = os.getenv("OPENAI_API_KEY")

if not GITHUB_TOKEN or not OPENAI_API_KEY:
    raise ValueError("Missing GITHUB_TOKEN or OPENAI_API_KEY")

GH_HEADERS = {
    "Authorization": f"token {GITHUB_TOKEN}",
    "Accept": "application/vnd.github+json",
}

OPENAI_HEADERS = {
    "Authorization": f"Bearer {OPENAI_API_KEY}",
    "Content-Type": "application/json",
}

def ask_chatgpt(prompt):
    payload = {
        "model": "gpt-4",
        "messages": [{"role": "user", "content": prompt}],
    }
    resp = requests.post(
        "https://api.openai.com/v1/chat/completions",
        headers=OPENAI_HEADERS,
        json=payload,
    )
    if resp.status_code != 200:
        print(f"OpenAI error: {resp.status_code} {resp.text}")
        return None
    return resp.json()["choices"][0]["message"]["content"]

def get_user():
    resp = requests.get("https://api.github.com/user", headers=GH_HEADERS)
    if resp.status_code != 200:
        print(f"GitHub error: {resp.status_code}")
        return None
    return resp.json()

def list_repos(limit=10):
    resp = requests.get(
        f"https://api.github.com/user/repos?per_page={limit}",
        headers=GH_HEADERS,
    )
    if resp.status_code != 200:
        print(f"GitHub error: {resp.status_code}")
        return []
    return resp.json()

def get_issues(owner, repo_name):
    resp = requests.get(
        f"https://api.github.com/repos/{owner}/{repo_name}/issues?state=open",
        headers=GH_HEADERS,
    )
    if resp.status_code != 200:
        print(f"GitHub issue fetch error: {resp.status_code}")
        return []
    return resp.json()

def comment_on_issue(owner, repo_name, issue_number, comment):
    payload = {"body": comment}
    resp = requests.post(
        f"https://api.github.com/repos/{owner}/{repo_name}/issues/{issue_number}/comments",
        headers=GH_HEADERS,
        json=payload,
    )
    if resp.status_code != 201:
        print(f"Error commenting: {resp.status_code} {resp.text}")
        return None
    return resp.json()

def create_issue(owner, repo_name, title, body):
    payload = {"title": title, "body": body}
    resp = requests.post(
        f"https://api.github.com/repos/{owner}/{repo_name}/issues",
        headers=GH_HEADERS,
        json=payload,
    )
    if resp.status_code != 201:
        print(f"Error creating issue: {resp.status_code} {resp.text}")
        return None
    return resp.json()

def main():
    user = get_user()
    if not user:
        print("Could not authenticate to GitHub")
        return

    username = user["login"]
    repos = list_repos(limit=5)

    repo_names = [r["full_name"] for r in repos]
    print(f"Connected as: {username}")
    print(f"Repos: {repo_names}")

    prompt = f"""
You are an autonomous GitHub assistant for user {username}.

These are the repositories available:
{json.dumps(repo_names)}

Decide on one of the following:
1. Review open issues and comment helpfully
2. Create a documentation issue
3. Do nothing

Return ONLY this format:
ACTION: 1|2|3
REASON: brief reason
"""

    decision = ask_chatgpt(prompt)
    if decision is None:
        print("No decision from ChatGPT")
        return

    print(f"Decision:\n{decision}")

    if "ACTION: 1" in decision:
        for repo in repos[:2]:
            owner, repo_name = repo["full_name"].split("/")
            issues = get_issues(owner, repo_name)
            if not issues:
                continue

            for issue in issues[:1]:
                issue_text = f"Title: {issue['title']}\nBody: {issue.get('body', '')}"
                comment_prompt = f"""
Write a brief, helpful comment for this GitHub issue.
Be professional and concise.

Issue:
{issue_text}
"""
                comment = ask_chatgpt(comment_prompt)
                if comment:
                    comment_on_issue(owner, repo_name, issue["number"], comment)
                    print(f"Commented on {owner}/{repo_name}#{issue['number']}")

    elif "ACTION: 2" in decision:
        for repo in repos[:1]:
            owner, repo_name = repo["full_name"].split("/")
            title = "Add better documentation"
            body = ask_chatgpt(
                f"Write a clear GitHub issue body asking to improve documentation for {repo_name}."
            ) or "Improve documentation for this repository."
            create_issue(owner, repo_name, title, body)
            print(f"Created documentation issue in {repo['full_name']}")

    else:
        print("No action needed")

if __name__ == "__main__":
    main()

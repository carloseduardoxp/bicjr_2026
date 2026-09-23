import os
import requests
import pandas as pd
from urllib.parse import urlparse

CSV_FILE = 'data.csv'
OUTPUT_DIR = '../output_classes'
GITHUB_TOKEN = 'YOUR_API_TOKEN'  
HEADERS = {'Authorization': f'token {GITHUB_TOKEN}'}

def baixar_arquivo_pr(repo_full, pr_number, file_path, dest_path, idx):
    pr_url = f"https://api.github.com/repos/{repo_full}/pulls/{pr_number}"
    resp = requests.get(pr_url, headers=HEADERS)

    if resp.status_code != 200:
        print(f"[ERRO {idx}] Falha ao acessar PR: {pr_url}")
        return False

    head_sha = resp.json()["head"]["sha"]

    raw_url = (
        f"https://raw.githubusercontent.com/"
        f"{repo_full}/{head_sha}/{file_path}"
    )

    content = requests.get(raw_url, headers=HEADERS)

    if content.status_code == 200:
        with open(dest_path, "w", encoding="utf-8") as f:
            f.write(content.text)

        print(f"[OK {idx}] {file_path}")
        return True

    print(f"[ERRO {idx}] Falha ao baixar: {raw_url}")
    return False

df = pd.read_csv(CSV_FILE)
os.makedirs(OUTPUT_DIR, exist_ok=True)

for idx, row in df.iterrows():
    nome_classe = row['Nome da classe'].strip()
    url_pr = row['URL pull request'].strip()

    try:
        parts = urlparse(url_pr)
        owner, repo, _, pr_number = parts.path.strip('/').split('/')[:4]
        repo_full = f"{owner}/{repo}"
    except Exception:
        print(f"[ERRO] Não foi possível processar a URL: {url_pr}")
        continue

    indice = idx + 2
    pasta_id = os.path.join(OUTPUT_DIR, f"id_{indice}")

    if os.path.exists(pasta_id) and os.listdir(pasta_id):
        #print(f"[INFO] Pasta {pasta_id} já contém arquivos. Pulando download.")
        continue

    os.makedirs(pasta_id, exist_ok=True)
    dest_path = os.path.join(pasta_id, os.path.basename(nome_classe))

    baixar_arquivo_pr(repo_full, pr_number, nome_classe, dest_path,indice)
import csv
import re
import mysql.connector

CSV_FILE = "../1.1 AIDEV/pull_requests_merged.csv"

DB_CONFIG = {
    "host": "localhost",
    "user": "root",
    "password": "",
    "database": "dataset_vem_2026"
}

# Ex.: https://github.com/Metta-AI/metta/pull/1688
PATTERN = re.compile(r"https://github\.com/([^/]+/[^/]+)/pull/\d+")

repositorios = set()

# Lê o CSV
with open(CSV_FILE, encoding="utf-8-sig") as f:
    reader = csv.DictReader(f)

    for row in reader:
        url = row["html_url"].strip()

        m = PATTERN.match(url)
        if m:
            repositorios.add(m.group(1))
        else:
            print(f"URL inválida: {url}")

print(f"{len(repositorios)} repositórios únicos encontrados.")

# Conecta ao MySQL
conn = mysql.connector.connect(**DB_CONFIG)
cursor = conn.cursor()

sql = """
REPLACE INTO repositories (owner_repo)
VALUES (%s)
"""

cursor.executemany(
    sql,
    [(repo,) for repo in repositorios]
)

conn.commit()

print(f"{cursor.rowcount} registros inseridos.")

cursor.close()
conn.close()
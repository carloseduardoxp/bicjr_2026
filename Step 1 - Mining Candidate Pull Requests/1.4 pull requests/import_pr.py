import csv
import re

import mysql.connector
from mysql.connector import Error


CSV_FILE = "../1.1 AIDEV/pull_requests_merged.csv"

DB_CONFIG = {
    "host": "localhost",
    "port": 3306,
    "user": "root",
    "password": "",
    "database": "dataset_vem_2026",
    "charset": "utf8mb4"
}


# Exemplo:
# https://github.com/Metta-AI/metta/pull/1688
PR_URL_PATTERN = re.compile(
    r"^https?://github\.com/"
    r"(?P<owner>[^/]+)/"
    r"(?P<repo>[^/]+)/pull/"
    r"(?P<pr_number>\d+)/?"
    r"(?:\?.*)?$",
    re.IGNORECASE
)


def extrair_dados_url(url):
    url = url.strip()

    match = PR_URL_PATTERN.match(url)

    if not match:
        return None

    owner = match.group("owner")
    repo = match.group("repo")
    pr_number = int(match.group("pr_number"))

    owner_repo = f"{owner}/{repo}"

    # Remove parâmetros e barra final para guardar a URL padronizada
    url_padronizada = (
        f"https://github.com/{owner_repo}/pull/{pr_number}"
    )

    return owner_repo, pr_number, url_padronizada


def main():
    conexao = None
    cursor = None

    try:
        conexao = mysql.connector.connect(**DB_CONFIG)
        cursor = conexao.cursor()

        sql_verificar_repositorio = """
            SELECT 1
            FROM repositories
            WHERE owner_repo = %s
              AND isFork = 0
              AND stars is not null
            LIMIT 1
        """

        sql_inserir_pr = """
            INSERT INTO pullRequests
            (
                owner_repo,
                pr_number,
                url,
                agent
            )
            VALUES (%s, %s, %s, %s)
            ON DUPLICATE KEY UPDATE
                url = VALUES(url),
                agent = VALUES(agent)
        """

        inseridos = 0
        atualizados_ou_inseridos = 0
        ignorados_repositorio = 0
        urls_invalidas = 0
        duplicados_csv = 0

        prs_processadas = set()

        with open(
            CSV_FILE,
            mode="r",
            encoding="utf-8-sig",
            newline=""
        ) as arquivo:

            leitor = csv.DictReader(arquivo)

            colunas_obrigatorias = {"agent", "html_url"}
            colunas_encontradas = set(leitor.fieldnames or [])

            if not colunas_obrigatorias.issubset(colunas_encontradas):
                raise ValueError(
                    "O CSV precisa conter as colunas "
                    "'agent' e 'html_url'. "
                    f"Colunas encontradas: {colunas_encontradas}"
                )

            for numero_linha, linha in enumerate(leitor, start=2):
                agent = (linha.get("agent") or "").strip()
                html_url = (linha.get("html_url") or "").strip()

                dados_url = extrair_dados_url(html_url)

                if dados_url is None:
                    print(
                        f"Linha {numero_linha} ignorada: "
                        f"URL inválida: {html_url}"
                    )
                    urls_invalidas += 1
                    continue

                owner_repo, pr_number, url = dados_url

                chave_pr = (owner_repo.lower(), pr_number)

                if chave_pr in prs_processadas:
                    duplicados_csv += 1
                    continue

                prs_processadas.add(chave_pr)

                cursor.execute(
                    sql_verificar_repositorio,
                    (owner_repo,)
                )

                repositorio_valido = cursor.fetchone()

                if repositorio_valido is None:
                    ignorados_repositorio += 1
                    continue

                cursor.execute(
                    sql_inserir_pr,
                    (
                        owner_repo,
                        pr_number,
                        url,
                        agent or None
                    )
                )

                atualizados_ou_inseridos += 1

                if cursor.rowcount == 1:
                    inseridos += 1

        conexao.commit()

        print("\nProcessamento concluído.")
        print(
            f"PRs inseridas ou atualizadas: "
            f"{atualizados_ou_inseridos}"
        )
        print(f"Novas PRs inseridas: {inseridos}")
        print(
            f"Ignoradas por repositório não elegível: "
            f"{ignorados_repositorio}"
        )
        print(f"URLs inválidas: {urls_invalidas}")
        print(f"Duplicadas no CSV: {duplicados_csv}")

    except (Error, OSError, ValueError) as erro:
        if conexao is not None:
            conexao.rollback()

        print(f"Erro: {erro}")

    finally:
        if cursor is not None:
            cursor.close()

        if conexao is not None and conexao.is_connected():
            conexao.close()


if __name__ == "__main__":
    main()
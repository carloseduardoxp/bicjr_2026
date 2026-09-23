import csv
import glob
import os
import re
import mysql.connector

DATA_CSV = "data.csv"
OUTPUT_DIR = "../Step 2 - Extract Classes/output_classes"

DB_CONFIG = {
    "host": "localhost",
    "user": "root",
    "password": "",
    "database": "dataset_vem_2026"
}


def extrair_numero_pr(url):
    """
    Exemplo:
    https://github.com/apache/pulsar/pull/24542
    -> 24542
    """
    match = re.search(r"/pull/(\d+)", url)
    return match.group(1) if match else None

def inteiro_ou_null(valor):
    if valor is None:
        return None

    valor = valor.strip()

    if valor == "" or valor.lower() == "null":
        return None

    return int(valor)

def main():
    conexao = mysql.connector.connect(**DB_CONFIG)
    cursor = conexao.cursor()

    sql_insert = """
        INSERT INTO code_smells (
            REPOSITORIO,
            Nome_da_classe,
            URL,
            pull_request,
            AGENTE,
            rule_key,
            start_line,
            end_line,
            severity,
            message,
            type
        )
        VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
    """

    total_inseridos = 0

    with open(DATA_CSV, "r", encoding="utf-8-sig", newline="") as arquivo_data:

        reader = csv.DictReader(arquivo_data)

        # DictReader já consumiu o cabeçalho.
        # Portanto, a primeira linha de dados é a linha 2.
        for numero_linha, row in enumerate(reader, start=2):

            repositorio = row["REPOSITORIO"].strip()
            nome_classe = row["Nome da classe"].strip()
            url = row["URL pull request"].strip()
            agente = row["AGENTE"].strip()

            pull_request = extrair_numero_pr(url)

            pasta_id = os.path.join(
                OUTPUT_DIR,
                f"id_{numero_linha}"
            )

            print(f"\n[{numero_linha}] {repositorio}")
            print(f"  Classe: {nome_classe}")
            print(f"  PR: {pull_request}")
            print(f"  Pasta: {pasta_id}")

            if not os.path.isdir(pasta_id):
                print("  -> Pasta não encontrada.")
                continue

            # Procura arquivos sonarLint*.csv
            padrao = os.path.join(
                pasta_id,
                "sonarLint*.csv"
            )

            arquivos_sonar = glob.glob(padrao)

            if not arquivos_sonar:
                print("  -> Nenhum sonarLint*.csv encontrado.")
                continue

            for arquivo_sonar in arquivos_sonar:

                print(f"  -> Lendo {arquivo_sonar}")

                with open(
                    arquivo_sonar,
                    "r",
                    encoding="utf-8-sig",
                    newline=""
                ) as arquivo:

                    sonar_reader = csv.DictReader(arquivo)

                    for smell in sonar_reader:

                        rule_key = smell.get("rule key")
                        start_line = smell.get("start line")
                        end_line = smell.get("end line")
                        severity = smell.get("severity")
                        message = smell.get("message")
                        tipo = smell.get("type")

                        start_line = inteiro_ou_null(smell.get("start line"))
                        end_line = inteiro_ou_null(smell.get("end line"))

                        # "null" textual -> NULL do MySQL
                        if tipo and tipo.strip().lower() == "null":
                            tipo = None

                        valores = (
                            repositorio,
                            nome_classe,
                            url,
                            pull_request,
                            agente,
                            rule_key,
                            start_line,
                            end_line,
                            severity,
                            message,
                            tipo
                        )

                        cursor.execute(sql_insert, valores)

                        total_inseridos += 1

            # Commit a cada projeto
            conexao.commit()

    cursor.close()
    conexao.close()

    print("\n===================================")
    print("Processamento concluído.")
    print(f"Total de code smells: {total_inseridos}")
    print("===================================")


if __name__ == "__main__":
    main()
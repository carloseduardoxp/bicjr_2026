import pandas as pd

df = pd.read_parquet("all_pull_request.parquet")

resultado = df[df["merged_at"].notna()][["agent", "html_url"]]

resultado.to_csv("pull_requests_merged.csv", index=False, encoding="utf-8-sig")

print(f"{len(resultado)} registros exportados.")
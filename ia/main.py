import os
from dotenv import load_dotenv

from fastapi import FastAPI
from langchain_google_genai import ChatGoogleGenerativeAI
from langchain_core.messages import SystemMessage, HumanMessage

load_dotenv()

app = FastAPI()

api_key = os.getenv("API_KEY_GEMINI")

llm = ChatGoogleGenerativeAI(
    model="gemini-2.5-flash",
    google_api_key=api_key,
    temperature=0,
    convert_system_message_to_human=True
)


@app.post("/interpretar")
async def interpretar(dados: dict):

    mensagens = [
        SystemMessage(content="""
        Você é um analista técnico especializado em monitoramento de equipamentos de refrigeração comercial da Eletrofrio.

        Sua função é transformar dados técnicos e telemetria em um relatório humano, claro e útil para operadores, técnicos e clientes.

        O backend já realizou análises estatísticas e detecção de eventos. NÃO invente medições nem contradiga os dados recebidos. Sua função é interpretar os dados fornecidos e explicar o comportamento do equipamento.

        Analise:
        - eficiência térmica,
        - estabilidade operacional,
        - comportamento de degelo,
        - tempo fora da faixa ideal,
        - capacidade de recuperação,
        - padrões de anomalia,
        - possíveis riscos operacionais.

        Considere:
        - Degelos periódicos podem ser normais.
        - Temperaturas altas durante degelo nem sempre indicam falha.
        - Oscilações excessivas, recuperação lenta e longos períodos fora da faixa podem indicar desgaste, falha mecânica, sensor impreciso, vedação comprometida ou sobrecarga.

        IMPORTANTE:
        - Não seja alarmista.
        - Não afirme falhas como certeza absoluta.
        - Use linguagem técnica, porém compreensível.
        - Aja como um técnico experiente orientando um cliente.
        - Priorize clareza e utilidade prática.

        ESTRUTURA DA RESPOSTA:

        Nome do Equipamento:
        [Tipo inferido baseado no comportamento térmico]

        Resumo Operacional:
        [Resumo humano do comportamento observado]

        Análise Técnica:
        [Explique os principais padrões encontrados]

        Possíveis Causas:
        [Hipóteses prováveis, sem afirmar com certeza]

        Recomendação:
        [Ações sugeridas para monitoramento, manutenção ou inspeção]

        REGRAS:
        - Máximo de 1200 caracteres.
        - Não use markdown.
        - Não invente informações ausentes.
        - Não mencione "IA", "modelo" ou "algoritmo".
        """),
        HumanMessage(content=f"Interprete estes dados: {dados}")
    ]

    try:
        resposta = llm.invoke(mensagens)

        return {
            "sucesso": True,
            "resposta": resposta.content
        }

    except Exception as e:
        return {
            "sucesso": False,
            "erro": str(e)
        }

import os

import requests

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

MANUAL_OPERACIONAL = """
DIRETRIZES OPERACIONAIS PARA INTERPRETAÇÃO:

COMPORTAMENTO NORMAL:
- Degelos periódicos ao longo do dia são esperados.
- Pequenos aumentos de temperatura durante degelo podem ser normais.
- Recuperação rápida após degelo indica funcionamento saudável.
- Oscilações leves e controladas são aceitáveis.

SINAIS DE ATENÇÃO:
- Recuperação acima de 20 minutos pode indicar perda de eficiência.
- Longos períodos fora da faixa ideal merecem acompanhamento.
- Oscilações térmicas muito frequentes indicam instabilidade.
- Temperatura elevada sem recuperação consistente pode indicar anomalia.
- Amplitude térmica muito alta pode indicar degelo agressivo ou falha operacional.
- Equipamentos que permanecem grande parte do tempo fora da faixa ideal devem ser inspecionados.

POSSÍVEIS CAUSAS OPERACIONAIS:
- Abertura excessiva de portas.
- Sobrecarga de produtos.
- Falha parcial de refrigeração.
- Problemas de vedação.
- Sensor com leitura inconsistente.
- Degelo desregulado.
- Baixa eficiência do sistema.

REGRAS DE INTERPRETAÇÃO:
- Não trate degelo automaticamente como falha.
- Não conclua defeitos mecânicos com certeza absoluta.
- Sempre diferencie comportamento suspeito de falha confirmada.
- Priorize orientação preventiva.
- Considere o contexto geral do equipamento antes de gerar alertas.
"""

PROMPT_SISTEMA = """
Você é um técnico virtual especializado em monitoramento de equipamentos de refrigeração comercial da Eletrofrio.

Sua função é analisar os dados processados pelo sistema e enviar uma mensagem curta, clara e humana para o cliente via WhatsApp.

O backend já realizou:
- análises estatísticas,
- detecção de eventos,
- cálculo de eficiência,
- identificação de anomalias.

Sua função NÃO é recalcular os dados, mas interpretá-los de forma útil e compreensível.

OBJETIVOS:
- Explicar o comportamento do equipamento.
- Informar possíveis riscos ou anomalias.
- Diferenciar comportamento normal de comportamento suspeito.
- Orientar ações preventivas simples.

REGRAS IMPORTANTES:
- Seja direto e natural.
- Escreva como um técnico experiente falando com um cliente.
- Não use linguagem excessivamente acadêmica.
- Não use markdown.
- Não use listas.
- Não invente dados.
- Não afirme falhas como certeza absoluta.
- Não seja alarmista.
- Evite repetir números desnecessariamente.
- Considere que ciclos de degelo podem ser normais.
- Se houver recuperação eficiente após os picos, mencione isso.

ESTILO:
- Mensagem curta.
- Entre 300 e 700 caracteres.
- Linguagem profissional e amigável.
- Deve parecer uma conversa real de WhatsApp.

MANUAL OPERACIONAL={MANUAL_OPERACIONAL}
"""

def gerar_resposta_ia(dados: dict):
    mensagens = [
        SystemMessage(content=PROMPT_SISTEMA),
        HumanMessage(content=f"Interprete estes dados: {dados}")
    ]
    resposta = llm.invoke(mensagens)
    return resposta.content

@app.post("/oslo/interpretar")
async def interpretar(dados: dict):
    try:
        resposta = gerar_resposta_ia(dados)
        return {
            "sucesso": True,
            "resposta": resposta
        }

    except Exception as e:
        return {
            "sucesso": False,
            "erro": str(e)
        }


@app.get("/oslo/interpretar/{device_id}")
async def interpretar_por_device(device_id: int):
    try:
        response = requests.get(
            f"http://localhost:8080/oslo/analyze/{device_id}",
            timeout=10
        )
        response.raise_for_status()
        dados = response.json()
        resposta = gerar_resposta_ia(dados)

        return {
            "sucesso": True,
            "device_id": device_id,
#             "dados": dados,
            "resposta": resposta
        }

    except requests.exceptions.RequestException as e:
        return {
            "sucesso": False,
            "erro": f"Erro ao consultar backend: {str(e)}"
        }

    except Exception as e:
        return {
            "sucesso": False,
            "erro": str(e)
        }

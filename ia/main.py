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

        EXEMPLOS DE TOM:

        "Identificamos oscilações térmicas acima do padrão esperado durante os ciclos de degelo. O equipamento conseguiu recuperar a temperatura após os eventos, porém o tempo fora da faixa ideal ficou elevado. Isso pode indicar perda de eficiência no sistema de refrigeração ou necessidade de revisão preventiva."

        "Os dados indicam funcionamento geral estável, com degelos ocorrendo dentro do comportamento esperado. No momento não há sinais claros de falha crítica, mas recomendamos continuar acompanhando as oscilações térmicas."
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

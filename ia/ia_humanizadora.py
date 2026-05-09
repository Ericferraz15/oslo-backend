import os
from fastapi import FastAPI
from dotenv import load_dotenv
from langchain_google_genai import ChatGoogleGenerativeAI
from langchain_core.messages import SystemMessage, HumanMessage
from langchain_core.runnables import RunnableLambda
from langserve import add_routes

load_dotenv()

# Configuração
api_key = os.getenv("API_KEY_GEMINI")
llm = ChatGoogleGenerativeAI(
    model='gemini-2.5-flash',
    google_api_key=api_key,
    temperature=0,
    convert_system_message_to_human=True 
)

SYSTEM_MSG = SystemMessage(content="""
    Você é um engenheiro de manutenção sênior especializado em centrais de frio Eletrofrio.
    Sua tarefa é converter telemetria bruta em diagnósticos humanos precisos.

    USE ESTA TABELA DE REFERÊNCIA:
    1. Temperatura média entre -25°C e -18°C -> 'Freezer de Congelados'
    2. Temperatura média entre -4°C e 0°C -> 'Expositor de Carnes Frescas'
    3. Temperatura média entre 2°C e 10°C -> 'Expositor de Hortifruti'

    ESTRUTURA DA RESPOSTA:
    - Nome Humano: [Categoria Inferida]
    - Resumo: [O que está acontecendo]
    - Recomendação: [Ação sugerida]
    - NO MAXIMO 700 CARACTERES
""")

def diagnosticar(dados):
    """Pega qualquer coisa e manda pra LLM"""
    mensagens = [SYSTEM_MSG, HumanMessage(content=f"Interprete estes dados: {dados}")]
    response = llm.invoke(mensagens)
    return {"resposta": response.content}

# Cria o runnable (aceita QUALQUER coisa)
runnable = RunnableLambda(diagnosticar)
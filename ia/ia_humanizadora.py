import os 
from dotenv import load_dotenv
from langchain_google_genai import ChatGoogleGenerativeAI
from langchain_core.messages import SystemMessage, HumanMessage

load_dotenv()

api_key = os.getenv("API_KEY_GEMINI")
if not api_key:
    print("ERRO: Variável API_KEY_GEMINI não encontrada no .env")
    exit(1)


llm = ChatGoogleGenerativeAI(
    model='gemini-2.5-flash', 
    google_api_key=api_key,
    temperature=0,
    convert_system_message_to_human=True 
)

dados_equipamento = """{
    "stats": {
        "min": -18.0,
        "max": 5.0,
        "avg": -11.487900355871881,
        "current": -12.7,
        "amplitude": 23.0
    },
    "events": [
        {"tipo": "DEGELO", "confianca": 0.5, "evidencias": ["Status de degelo ativo no período"], "inicio": 54, "fim": 61},
        {"tipo": "TEMPERATURA_ALTA", "confianca": 0.5, "evidencias": ["Temperatura acima do threshold"], "inicio": 214, "fim": 237}
    ]
}"""

mensagens = [
    SystemMessage(content="""
        Você é um engenheiro de manutenção sênior especializado em centrais de frio Eletrofrio.
        Sua tarefa é converter telemetria bruta em diagnósticos humanos precisos.

        USE ESTA TABELA DE REFERÊNCIA:
        1. Temperatura média entre -25°C e -18°C -> 'Freezer de Congelados'
        2. Temperatura média entre -4°C e 0°C -> 'Expositor de Carnes Frescas'
        3. Temperatura média entre 2°C e 10°C -> 'Expositor de Hortifruti'

        ESTRUTURA DA RESPOSTA:
        - Nome Humano: [Categoria Inferida]
        - Resumo: [O que está acontecendo]
        - Recomendação: [Ação sugerida], 
        - NO MAXIMO 700 CARACTERES
    """),
    HumanMessage(content=f"Interprete estes dados: {dados_equipamento}")
]

try:
    resposta = llm.invoke(mensagens)
    print("\n=== RESPOSTA DA IA ===\n")
    print(resposta.content)
except Exception as e:
    print(f"Erro ao chamar a API: {e}")
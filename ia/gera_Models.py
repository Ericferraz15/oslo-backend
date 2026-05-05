import os
from dotenv import load_dotenv
from google import genai

load_dotenv()
api_key = os.getenv("API_KEY_GEMINI")

client = genai.Client(api_key=api_key)

print("📋 Modelos disponíveis para generateContent:\n")
for model in client.models.list():
    # O atributo correto é 'supported_actions' (não 'supported_methods')
    if hasattr(model, 'supported_actions') and 'generateContent' in model.supported_actions:
        print(f"✅ {model.name}")
    else:
        # Fallback para versões antigas
        try:
            if 'generateContent' in str(model):
                print(f"✅ {model.name}")
        except:
            pass
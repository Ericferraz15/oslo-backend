from ia_humanizadora import runnable
from fastapi import FastAPI
from langserve import add_routes

app = FastAPI()

# Adiciona as rotas do LangServe (SEM schema de validação)
add_routes(
    app, 
    runnable, 
    path="/diagnosticar",
    enable_endpoints=["invoke"]
)

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
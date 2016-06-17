# DistributedSystems-Project-16


# Final Grade: 18


Grupo de SD 7 - Campus Alameda

Daniel Fermoselle 78207 daniel.fermoselle@gmail.com

Tiago Rodrigues 78692 tiagomsr4@gmail.com

Pedro Ribeiro 79055 pedro.ribeiro.pr.95@gmail.com


Repositório:
[tecnico-distsys/A_07-project](https://github.com/tecnico-distsys/A_07-project/)

-------------------------------------------------------------------------------

## Instruções de instalação 


### Ambiente

[0] Iniciar sistema operativo
*Linux*


[1] Iniciar servidores de apoio

JUDDI: (Supondo já existente esta biblioteca na máquina de teste)
-Entrar na pasta com o executável para iniciar os servidores de apoio: cd juddi-3.3.2_tomcat-7.0.64_9090/bin
-Executar assim: ./startup.sh


[2] Criar pasta temporária

```
cd para uma directoria onde queira fazer a pasta temporária
mkdir nomeDaPastaTemporária
cd nomeDaPastaTemporária
```


[3] Obter código fonte do projeto (versão entregue)

```
$ git clone -b SD_R2 https://github.com/tecnico-distsys/A_07-project/

```


[4] Instalar módulos de bibliotecas auxiliares

```
Supondo já existente esta biblioteca na máquina de teste

Download de http://disciplinas.tecnico.ulisboa.pt/leic-sod/2015-2016/labs/09-crypto/crypto.zip
cd crypto
mvn clean install
cd uddi-naming (versão 1.1.1)
mvn clean install

```

### A ORDEM DE EXECUÇÃO DOS SERVIÇOS DEVE SER RESPEITADA, ISTO É, DEVE-SE CORRER
### PRIMEIRO A CA DEIXÁ-LA EM EXECUÇÃO DEPOIS INSTALAR O WS-HANDLERS 
### DEPOIS AS DUAS TRANSPORTADORAS E SÓ POR ULTIMO
### LIGAR O BROKER PRIMÁRIO E SECUNDÁRIO DEVIDO ÀS DEPENDÊNCIAS
-------------------------------------------------------------------------------

### Serviço CA

[1] Construir e executar **servidor**

```
cd ca-ws
mvn clean install
mvn exec:java

```

[2] Construir **cliente** e executar testes


Pré-condição Executar passo 1 do Serviço CA:

```
cd ca-ws-cli
mvn clean install

```

...

-------------------------------------------------------------------------------

### Serviço HANDLERS

[1] Construir e executar testes

```
Pré-condição Executar passo 1 do Serviço CA:
```

```
cd ws-handlers
mvn clean install

```

...

-------------------------------------------------------------------------------

### Serviço TRANSPORTER

[1] Construir e executar **servidor**



Pré-condição Executar passo 1 do Serviço CA e HANDLERS:

```
cd transporter-ws
mvn clean install
mvn exec:java -> para UpaTransporter1
mvn -Dws.i=2 exec:java -> para UpaTransporter2

```

[2] Construir **cliente** e executar testes

Pré-condição Executar passo 1 do Serviço TRANSPORTER:

```
cd transporter-ws-cli
mvn clean install

```

...

-------------------------------------------------------------------------------

### Serviço BROKER

[1] Construir e executar **servidor**



Pré-condição Executar passo 1 do Serviço TRANSPORTER, CA e HANDLERS:

```
cd broker-ws
mvn clean install
mvn exec:java -> para UpaBroker
mvn -Dws.i=1 exec:java -> para UpaBrokerSlave (a.k.a Servidor secundario)
```


[2] Construir **cliente** e executar testes

Pré-condição Executar passo 1 do Serviço BROKER:

```
cd broker-ws-cli
mvn clean install

Demonstração da replicação:
mvn exec:java

```

...

-------------------------------------------------------------------------------
**FIM**

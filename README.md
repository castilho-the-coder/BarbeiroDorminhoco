# 💈 Barbeiro Dorminhoco

Implementação didática do clássico problema de sincronização "Barbeiro Dorminhoco" usando **threads Java** 

## 📋 Descrição do Problema

O problema do Barbeiro Dorminhoco é um clássico da programação concorrente que modela a coordenação entre um barbeiro e clientes em uma barbearia com número limitado de cadeiras de espera:

- Um **barbeiro** atende clientes um de cada vez.
- Se não há clientes, o **barbeiro dorme**.
- Quando um **cliente chega**:
  - Se há espaço na fila, ele senta e aguarda.
  - Se o barbeiro está dormindo, o cliente o acorda.
  - Se a barbearia está cheia, o cliente vai embora.
- Após o atendimento, o cliente sai.

**Sincronização**: O desafio é evitar condições de corrida (race conditions) e deadlocks usando apenas mecanismos primitivos.

## 🏗️ Arquitetura do Projeto

### `Barbearia.java` — Monitor
Gerencia o estado compartilhado e coordena barbeiro + clientes:

- **Atributos**:
  - `numCadeiras`: número máximo de cadeiras de espera.
  - `filaDeEspera`: fila de clientes aguardando atendimento.
  - `aberta`: flag indicando se a barbearia ainda aceita clientes.

- **Métodos principais**:
  - `proximoClienteParaAtender()`: Barbeiro chama para obter próximo cliente (bloqueia se vazio).
  - `clienteQuerCortar(Cliente)`: Cliente chama para entrar na fila e aguardar.
  - `finalizarAtendimento(Cliente)`: Barbeiro chama ao terminar corte.
  - `fecharBarbearia()`: Encerra a simulação (novos clientes são recusados).

### `Barbeiro.java` — Thread do Barbeiro
Loop infinito:
1. Chama `proximoClienteParaAtender()` (bloqueante).
2. Simula corte com `Thread.sleep()` (fora do monitor).
3. Chama `finalizarAtendimento()` para acordar o cliente.
4. Retorna `null` → encerra quando barbearia fecha e fila vazia.

### `Cliente.java` — Thread do Cliente
Executa uma única vez:
1. Chama `barbearia.clienteQuerCortar(this)`.
2. Aguarda atendimento (via `wait()`).
3. Sai quando `atendido == true`.

### `Simulacao.java` — Programa Principal
- Cria a barbearia com `N_CADEIRAS = 3`.
- Inicia thread do `Barbeiro`.
- Gera clientes aleatoriamente durante 20 segundos.
- Fecha a barbearia e aguarda término de todas as threads.


## 🚀 Como Compilar e Executar

### Pré-requisitos
- Java Development Kit (JDK) 8 ou superior.
- Windows PowerShell (ou qualquer terminal com `javac` e `java`).

### Compilação

Abra o PowerShell na pasta do projeto:

```powershell
cd C:\Users\marce\BarbeiroDorminhoco-1
javac *.java
```

Isso gera arquivos `.class` (bytecode compilado).

### Execução

```powershell
java Simulacao
```

A simulação:
1. Abre a barbearia com 3 cadeiras de espera.
2. Inicia o barbeiro e gera clientes por 20 segundos.
3. Fecha a barbearia.
4. Aguarda atendimento dos clientes restantes.
5. Encerra com mensagem "Simulação encerrada".

### Exemplo de Saída

```
--- Barbearia aberta (N=3) ---
Barbeiro: Zzzzz... (sem clientes, vou dormir)
Cliente 1: Sentei para esperar. (1 na espera)
Cliente 1: Acordei o barbeiro!
Barbeiro: Chamando Cliente 1. (0 na espera)
Barbeiro: Cortando cabelo de Cliente 1...
Cliente 2: Sentei para esperar. (1 na espera)
Cliente 3: Sentei para esperar. (2 na espera)
Cliente 4: Barbearia cheia! Vou embora.
Barbeiro: Terminei o corte de Cliente 1
Cliente 1: Cabelo cortado! Indo embora.
Barbeiro: Chamando Cliente 2. (1 na espera)
... (mais saída)
--- Tempo esgotado: fechando a barbearia ---
Barbearia: Fechando as portas. Não aceitamos mais clientes.
Barbeiro: Barbearia fechada e sem clientes. Vou para casa.
--- Simulação encerrada ---
```

## 🔧 Configuração e Ajustes

### Duração da Simulação
Em `Simulacao.java`, linha ~27:

```java
int DURACAO_MS = 20_000; // 20 segundos
```

Altere para qualquer valor em milissegundos.

### Número de Cadeiras
Em `Simulacao.java`, linha ~9:

```java
int N_CADEIRAS = 3;
```

Aumente ou diminua conforme desejado.

### Tempo de Corte
Em `Barbeiro.java`, linha ~28:

```java
int tempoCorte = rand.nextInt(3000) + 1000; // 1 a 4 segundos
```

### Intervalo entre Chegada de Clientes
Em `Simulacao.java`, linha ~38:

```java
int intervalo = rand.nextInt(1500) + 100; // 100ms a 1.6s
```

## 📊 Conceitos de Sincronização Utilizados

### Monitor Pattern
- `synchronized (this)` protege regiões críticas.
- Apenas uma thread por vez acessa o monitor.

### Wait / Notify
- `wait()`: Thread libera o lock e dorme até ser notificada.
- `notifyAll()`: Acorda todas as threads esperando no monitor.
- Uso de `while` (não `if`) protege contra spurious wakeups.

### Volatile
- `private volatile boolean atendido`: Garante visibilidade de mudanças entre threads.
- `private volatile boolean aberta`: Sinaliza fechamento da barbearia.

### Race Condition Prevention
- Toda alteração de `filaDeEspera` ocorre dentro de `synchronized`.
- Verificação de estado + ação atômica (checar e entrar na fila).

## 📁 Estrutura de Arquivos

```
BarbeiroDorminhoco-1/
├── Barbearia.java        # Monitor (sincronização)
├── Barbeiro.java         # Thread do barbeiro
├── Cliente.java          # Thread do cliente
├── Simulacao.java        # Programa principal
├── README.md             # Este arquivo
└── *.class               # Bytecode compilado (não versionar)
```

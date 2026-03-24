# threadlearning

Java concurrency tanulóprojekt, amely egy közös bankszámlán végzett párhuzamos műveleteken keresztül mutatja be a különböző szálkezelési stratégiák viselkedését és teljesítményét — beleértve a valódi blokkoló HTTP I/O hatását.

## Technológiák

- Java 21
- Spring Boot 4.0.4 (parancssori alkalmazás, nincs web szerver)
- Java `HttpClient` (blokkoló HTTP hívások)

## Architektúra

A projekt Clean Architecture elveket követ:

```
domain/          – BankAccount, BankOperation, FamilyMember (üzleti logika)
application/     – use case-ek, port interfészek (OperationSource, SimulationRunner, OperationReporter)
infrastructure/  – runner implementációk, HTTP reporter, generátorok
config/          – SimulationProperties (@ConfigurationProperties)
```

## A szimuláció menete

Minden futáskor:
1. Egy közös `BankAccount` jön létre (szinkronizált shared resource)
2. A family memberek párhuzamosan végeznek deposit/withdraw műveleteket
3. Minden művelet után a rendszer HTTP POST-ot küld egy külső endpointra (`httpbin.org/post`) — ez a blokkoló I/O pont
4. A futás végén benchmark eredmény és egyenleg-konzisztencia ellenőrzés történik

## Runner típusok

| Runner | Leírás |
|---|---|
| `plain` | Minden family membernek saját platform szál, `Thread.join()` várakozás |
| `fixed` | Fix méretű thread pool, `ExecutorService.invokeAll()` |
| `virtual` | Feladatonként egy virtuális szál (Java 21), `newVirtualThreadPerTaskExecutor()` |

A virtuális szálak előnye a blokkoló HTTP hívások esetén mutatkozna meg: amíg egy virtuális szál a hálózati választ várja, a carrier thread más feladatot végezhet.
A teszt során nem mutatott jobb teljesítményt a fix méretű poollal szemben.

## Shared resource-ok

A szimuláció szándékosan két szinkronizált közös erőforrást tartalmaz:

- `BankAccount` – `synchronized` deposit/withdraw/getBalance műveletek
- `RandomOperationGenerator` – `synchronized` nextOperation, közös `Random` példánnyal

## Konfiguráció

`application.properties` vagy parancssori argumentumok:

```properties
simulation.member-count=5
simulation.operations-per-member=1000
simulation.initial-balance=10000
simulation.min-amount=100
simulation.max-amount=5000
simulation.runner-type=plain        # plain | fixed | virtual
simulation.pool-size=10             # csak fixed runner esetén releváns
simulation.operation-source-type=random
```

## Futtatás

```bash
./mvnw spring-boot:run \
  -Dspring-boot.run.arguments="
    --simulation.runner-type=virtual
    --simulation.member-count=50
    --simulation.operations-per-member=100"
```

## Benchmark kimenet

```
=== Benchmark result ===
Runner type: virtual
Member count: 50
Operations per member: 100
Total operations: 5000
Elapsed time (ms): 23202
Operations per second: 215,49
Consistent: true

=== Simulation result ===
Initial balance: 10000
Final balance: 49446
Total deposited amount: ...
Total withdrawn amount: ...
```

A `Consistent: true` azt jelenti, hogy a záró egyenleg megfelel a várható értéknek — a szinkronizáció helyes.

## Eredmények
Member count: 50
Operations per member: 100
Total operations: 5000

<img width="1440" height="798" alt="kép" src="https://github.com/user-attachments/assets/d7fb72a0-a235-4eb9-9ca7-347989ba4cf1" />



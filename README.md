# NCSU CSC 750 (Service Oriented Computing)

Project 3

Play framework server application that implements an OWL ontology. Ontology was created
using Protoge. This application allows access to the ontology through a REST API.

Ontology Schema:
Now we shall implement our own Ontology using Protege. Here are the assertions we want to
capture:
1. A Person is either a Merchant or a Consumer
2. A Transaction has one sender and one receiver (both Persons), both of which participate
in the transaction
3. A Commercial transaction is one where at least one of the sender and receiver is a
Merchant
4. A Personal transaction is one where both are Consumers
5. A Purchase transaction is a Commercial transaction where the receiver is a Merchant
and the sender is a Consumer
6. A Refund transaction is a Commercial transaction where the sender is a Merchant and
the receiver is a Consumer
7. A Merchant is Trusted if it participates in at least one Purchase transaction

OWL ontology file can be found [here](owl/transaction.owl)

For more detail including API specs, see [Assignment Instructions](doc/P3.pdf)

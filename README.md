smartgrid-topology-generator
============================
Building on a smart grid meta-model this generator is able to automatically create random but realistic smart grid communication topologies.
These can be used to analyze, simulate, design, compare, and improve smart grid infrastructures.
The meta-model is based on an analysis of a real-world smart grid topology.

Smart Grid Meta-Model
--------------
![alt tag](/lu.snt.smartgrid-topology-generator.model/meta-model.png)

The meta-model is a formal description of the topology characteristics.
It describes the concepts required to model an abstraction of a PLC smart grid communication topology.
It contains the topological entities as well as the physical communication structure.
The meta-model is central for the generator.
It defines all concepts and the structural definition of all possible topologies.
It is used as a template definition to which all generated random topologies the generator produces must conform.

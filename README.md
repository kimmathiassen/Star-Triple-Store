Github


This document describe the current state of the Star Triple Store as of September 22, 2017.
Any questions can be directed as kim@meyn.dk
I will do my best to answer.

I recommend to read this entire document before changing the codebase.

=== State of the project ===
This section describes the project as is and clarify important terms and design decisions.
This software is a triple store based on a vision from Olaf Hartig. 
He proposed an logical extension of RDF and SPARQL called RDF* and SPARQL*.
The concept is that a triple can contain an embedded triple at the subject or object position.
e.g. <<:obama a :president>> :trueInYear 2015^^xsd:integer
Similar constructs are possible for triple patterns.
He further envisioned that it would be possible to make a triple store that, at the physical storage level, allows representation of these embedded triple.

The project is an heavily modified extension of the Apache Jena project.

Below we explain the essential concepts:


== Extension of SPARQL ==

SPARQL* allow for embedded triple patterns.
Further the BIND keyword can now bind triples to a variable.
This means that an embedded triple pattern can be rewritten as such:

The query
Select ?year
WHERE { 
	<<:obama a :president>> :trueInYear ?year 
}

Can be rewritten to 
Select ?year
WHERE { 
	BIND (<<:obama a :president>> AS ?e) . 
	?e :trueInYear ?year 
}


== Dictionary ==
Each node of a triple, the subject, predicate or object, is encoded as a key.
An essential feature of the Star Triple Store is that all operations are conducted on keys (longs).
To determine if a triple pattern matches a triple, the keys from the triple pattern is compared to the keys of the triple.
This will result in at most three comparison operations, as oppose to a string representation of triples (as in standard Jena) this is significantly faster.

The mappings from a URI, Literal or blank node to a key is stored in the NodeDictionary.
To denote that an node has a key we write as following:
Node -> identifier (in base 10).
e.g. :obama -> 5

In addition to the NodeDictionary, three other dictionaries exist.
The VariableDictionary encode the variables of the triple patterns to integers.
Again, having a numeric representation of a variable makes the comparison much cheaper in most cases. 

The ReferenceDictionary encodes a special type of key. 
It maps from a reference key to a subject, predicate, and object key.
The notation is as follows: 
reference key -> (subject key,predicate key,object key)
Reference keys will be explained in the encoding subsection.

The Prefix dictionary, contains mappings from an prefix identifier(integer) to a string.
When triples are loaded into the star triple store, the system will try to find prefixes in in the URIs.


== Encoding ==
These base idea is to allocate a fixed amount of bits for representing a triple.
In this project we decided to use three times 64 bits to represent, i.e. 64 bits to represent each the subject, predicate, and object.

We have three types of encoding of these nodes(subject,predicate,object):

Simple keys:
These are used to represent the URIs, Literals, or blank nodes. 
The most significant bit (MSB) is always set to zero and the remaining 64 bits hold the identifier of the node.
The identifier is given by the dictionary, see next subsection. 
As an example imagine the URI ":obama", this is given the identifier 5.
The encoding/key will be as follows:
0-0000000000000000000000000000000000000000000000000000000000000101

Embedded keys:
These nodes represents a node that contains three other nodes.
The four MSBs is the header and the remaining 60 bits encode the subject, predicate, and object.
If the MSB is set to 1, it means the the encoding is an embedded node.
We decided that each node should be represented using 20 bits.
As an example imagine the following identifiers are given for the embedded node "<<:obama a :president>>"
:obama -> 5, a -> 2, :president -> 3.
The encoding/key will look as follows:
1000-00000000000000000101-00000000000000000010-00000000000000000011

Reference keys:
A reference node is created in two scenarios:
1) When one or more nodes are not able to be represented using the allocated number of bits, in this case 20, and is used in an embedded triple.
Imagine that :obama received the identifier :obama -> 1048576 (a number that would require 21 bits), then it would not be possible to use the identifier in an embedded key.
Therefore the embedded node is represented using a reference key instead.

2) When an embedded triple contains another embedded triple (i.e. nested embedded triples). 
Because an embedded key needs 64 bits to be encoded, it is not possible to encode it using 20 bits.

The four MSB is the header, where the two MSB is set to 1, and the remaining 60 bits stores a special identifier provided by the Reference Dictionary.

Imagine the following embedded node "<<:obama a :president>>":
The identifiers are as follows: obama -> 1048576, a -> 2, :president -> 3.
The encoding/key will be as follows:
1100-000000000000000000000000000000000000000000000000000000000001

The reference dictionary will hold the following entry:
1100-000000000000000000000000000000000000000000000000000000000001 -> (1048576,2,3) [these are written as 10-base numbers for brevity]

In case (2) the right side of the reference dictionary would contains another reference key in the subject or object position 


== Index == 
The system contains three in-memory indexes: a SPO, POS, OSP.

SPO answers triple patterns: SP* and S**
         
OSP answers triple patterns: OS* and O**
         
POS answers triple patterns: PO* and P**


== Query optimization ==
Before I can explain the query optimizations I need to explain how Jena performs query evaluation and optimization.
Jena use iterators when evaluating queries.
This means that when a triple pattern is being evaluated, as oppose returning a set of matching triples, an iterator of the triples are returned.
The iterator is passed to the next query operator to be further restricted.
This entails some pros and cons.
The cons are:
- The query plan must be a left deep tree
- No parallel processing is possible
The Pros are:
- Minimum set of intermediate results
- Easy to create query plans

Jena have two steps of query optimization, the first is logical query optimization and the second physical query optimization.
In the the logical step jena will "streamline" the query plan based on some conventions and heuristics.
The physical step is done by the actual store, e.g. TDB. Here the order of query operators are changed based on the actual selectivity.

The Star Triple Store functions a bit different.
It use heuristics to create the the query tree.
As a rule, we push the triple patterns with the highest selectivity to the bottom.
We do not measure the actual selectivity of a triple pattern but use the heuristics described in this paper ()
As a guideline, we say that the more variables a triple pattern contain, the less selective it is.
This means that the triple pattern (s,p,o) is the most selective and the lest selective is (?,?,?).
If two triple patterns have an equals number of variables we say that there is the following order:
o > s > p
The logic behind this is, there are only a few distinct predicate, thus a triple pattern with only a predicate will match a large number of triple.
A triple pattern with only a subject, will match more triples than a triple pattern with only a predicate.
Following this logic objects are more selective than subject, because several triples often share a subject, while objects tends to be unique.
The order of all triple patterns are as follows:

(*,*,*) as t
(s,p,o)
(s,?,o) as ?
(s,?,o)
(?,p,o) as ?
(?,p,o)
(s,p,?) as ?
(s,p,?)
(?,?,o) as ?
(?,?,o)
(s,?,?) as ?
(s,?,?)
(?,p,?) as ?
(?,p,?)
(?,?,?) as ?
(?,?,?)

If the there exist value for a bind variable, then we can compute the s,p, and o. 
In the top triple pattern we use "*" to note that it does not matter if the s,p or o, is a value or a variable.
Hereon after, if a triple pattern is bound to a variable, then we say it is more "selective".
This is because, we gain more "information", due to solution mappings being created for the bind variable and the actual selectivity is still the same. 

In the case where two triple patterns are tied in selectivity. 
We look at the same values, if one of them is a reference key, we break the tie in the favor in the triple pattern without a reference key.
The reason for this is because there is an overhead associated with reference keys.

== Turtle Star ==
In order to serialize RDF* we created Turtle Star (extension .ttls).
It uses the same syntax a turtle and it only differs in a few places.
An element of a triple can now be another triple. 
e.g. <<<http://test.com/obama> a :president>> :trueInYear 2015^^xsd:integer
Multiple levels of nested embedded triples are allowed.

== Custom reference key distribution ==
Reference keys are normally only created if an embedded key contains an key (subject, predicate, or object) that cannot be represented in the allocated number of bits.
For testing purposes, we have introduced a flag that allows the tester to artificially change the number of reference keys.
Currently, this option is only possible to enable by modifying the Triple Star Store. 
To set a custom reference key distribution invoke the following method: 
NodeDictionaryFactory.getDictionary().setReferenceTripleDistribution(<percentage>)
where the <percentage> is an integer number between 0-100 corresponding to the percentage distribution of reference and non-reference keys.





=== Software structure ===
This section explain the software on a package level.
It may be useful to reference the Class Diagram while reading this section, 
despite the fact that this section talk about packages and the class diagram is about the classes.


== Main ==
This is the entry point of the code.
It parses the input parameters and makes them available for the rest of the program.
Here the query engine and triple parser is registered.

== TurtleStar ==
This package contains all logic for parsing turtleStar files. 

== Node ==
The turtle star parser produce Jena triples.
These triples consist of nodes, this package contains the classes of these nodes.
These nodes are only used when the data is loaded into the system and when printing the result set. (exceptions can happen)

== Dictionary ==
This package contains the three(four) dictionaries: NodeDictionary, ReferenceDictionary, VariableDictionary, and PrefixDictionary.

The NodeDictionary is by far the most important.
It contains the mappings from URIs,Literals, and Blank Nodes to the bit encoding. 
It terms of classes it maps from StarNodes to Keys (and the reverse).
There are two implementations of the NodeDictionary.

1) The HashNodeDictionary, is a in-memory dictionary that use HashMaps to store the mappings.
It is very fast but it is limited to the memory budget of the JVM.

2) The BTreeHybridDictionary, is an in-memory and on-disk dictionary. 
It will store entries in a in-memory buffer until a certain limit is reached, at this point it will write all entires to disk and empty the buffer.
This limit is defined in the Config class.
The in-memory buffer is implemented as a BTreeMap, this is from a project called mapDB.
The data store on disk is saved in a BTree structure, this is from a project called mapDB.
While this is not as fast as the HashNodeDictionary, it is possible to use very large datasets.
If the dictionary is not closed when done, the file might be corrupted and have to be deleted before the software can be run again.
Currently, an existing db cannot be used.

== Triple ==
This package defines a triple (not the same as a Jena triple).
A triple consists of three keys. Keys are also define in this package.

== Graph ==
Contains the logic of a graph. 
A graph is the abstraction over the internal storage.

== TripleStore ==
This package and its three subpackages (flatindex,hashindex,treeindex)
It define the triple store, the three types of indexes, and the keyContainer.
Due to a memory optimization the triple store does not contains triples, but keyContainers. 
A keyContainer is a "triple" that does not need to contain the subject, predicate, and object.
This is useful because it gives some flexibility in the implementation of the index.

The hashIndex
is a hashmap, containing a hashmap, containing a array of keyContainers.
They key container only contains one element of a triple, e.g. the object.
Each hashmap have a key that corresponds to a triple element. 
E.g. the subject and predicate key.
When returning an iterator of keycontainers, the keys of the hashmap is added to the keycontainer. 
This ensures that the subject, predicate, and object is present.

The TreeIndex
is similar to the HashIndex, it simply use a tree structure instead of a hashmap.
It takes up less memory but is slower than the hashIndex.

The Flatindex
Is designed to have a simple data structure at the cost of storing duplicate data.
Each triple is stored twice.
E.g. <s> <p> <o> is stored as follows
sp -> {o}
s -> {po}
where the left side is the key and the right side is a list of keyContainers.

All index are stored in memory.

== QueryParser ==
This package handles the parsing of sparql* queries

== TriplePattern ==
The where clause of a query contains one or more triple patterns.
Triple patterns consists of elements. 
Elements can be variables, keys, and other triple patterns.

== QueryEngine ==
This package contains the logical that evaluate the query over the store.
It contains classes for evaluating the query operators.

== Transform ==
This package is responsible for the query optimization.
It creates a new query plan as described in Section "Query optimization"

== ResultSerilizer ==
This package contains classes used to print the result of a query after it has been evaluiated.
It is important to always print the result, because the class ResultSet only contains bindings (solution mappings), and they need to be evaluated.


== Helper ==
This package contains helper classes.


=== Class diagram ===
Below is the class digram of the Star Triple Store.
It is not an exhaustive diagram, several classes have been omitted.
Either because they are classes for creating other objects, i.e. factories or builders.
Or because they do not "fint" in the diagram.


     +----------------------------+                               +----------------------+         +--------------------+
     |                            |                               |                      |         |                    |
     |  Composition       +----<> |                               |   AbstractNodeDict   <l--------+  BTreePhysicalDict |
     |                            |                               |                      |     |   |                    |
     |  Inheritance       +----l> |                               +----------------------+     |   +--------------------+
     |                            |                                          |                 |
     |  Association       +-----> |                                          _                 |   +--------------------+
     |                            |                               +----------v-----------+     |   |                    |
     +----------------------------+                               |                      |     +---+ HashNodeDictionary |
                                                                  |     NodeDictionary   |         |                    |           +--------------------+
                                                                  |                      |         +--------------------+           |                    |
                                                                  +----------------------+                                      +---+   FlatIndex        |
                                                                             |                                                  |   |                    |
                                                                          Used by                                               |   +--------------------+
                                                                             |                                                  |
                                  +--------------------+          +----------v-----------+        +---------------------+       |   +--------------------+
                                  |                    |          |                      |        |                     |       |   |                    |
                                  |        Graph       <>---------+    TripleStore       <>-------+      Index          <l----------+   HashIndex        |
                                  |                    |          |                      |        |                     |       |   |                    |
                                  +---------^----------+          +----------------------+        +---------------------+       |   +--------------------+
                                            v                                                                                   |
                                            |                                                                                   |   +--------------------+
                                            |                                                                                   |   |                    |
     +--------------------+       +--------------------+          +-----------------------+       +----------------------+      +---+  TreeIndex         |
     |                    |       |                    |  Load    |                       |       |                      |          |                    |
     |   MyTransform      |       |      Model         <--Into----+       Triple          <>------+     StarNode         |          +--------------------+
     |                    |       |    Jena Class      |          |     Jena Class        |       |                      |
     +--------------------+       +---------^----------+          +-----------------------+       +----------^-----------+          +--------------------+
               |                            |                                                                _                      |                    |
               |                          Has a                                                              +----------------------+  EmbeddedNode      |
               ^                            |                                                                |                      |                    |
     +---------v----------+       +--------------------+          +-----------------------+       +-----------------------+         +--------------------+
     |                    |       |                    |          |                       |       |                       |
     |   QueryEngineStar  +------<>   QueryExecution   +-Create--->     ResultSet         |       |   SimpleNode          |         +--------------------+
     |                    |       |                    |          |    Jena Class         |       |                       |         |                    |
     +--------------------+       +----------^---------+          +-----------------------+       +----------^------------+     +---+ SimpleURINode      |
                                             |                               |                               _                  |   |                    |
                                             |                              Use                              |                  |   +--------------------+
                                             |                               |                               |                  |
                                             |                    +----------v------------+                  |                  |   +--------------------+
                                            Use                   |                       |                  |                  |   |                    |
                                             |                    |   ResultSerilizer     |                  +----------------------+ SimpleLiteralNode  |
                                             |                    |                       |                                     |   |                    |
                                             |                    +-----------------------+                                     |   +--------------------+
                                             |                                                                                  |
                                             |                                                                                  |   +--------------------+
                                             |                                                                                  |   |                    |
                                  +--------------------+          +-----------------------+                                     +---+ SimpleBlankNode    |
                                  |                    |          |                       |                                         |                    |
                                  |     Query          <>---------+    TriplePattern      |           +--------------------+        +--------------------+
                                  |    Jena Class      |          |                       |           |                    |
                                  +--------------------+          +-----------^-----------+       +---+   TriplePattern    |
                                                                              v                   |   |                    |
                                                                              |                   |   +--------------------+
                                                                              |                   |
                                                                  +------------------------+      |   +--------------------+
                                                                  |                        |      |   |                    |
                                                                  |     Element            <l---------+   Variable         |
                                                                  |                        |      |   |                    |
                                                                  +------------------------+      |   +--------------------+
                                                                                                  |
                                                                                                  |   +--------------------+
                                                                                                  |   |                    |
                                                                                                  +---+   Key              |
                                                                                                      |                    |
                                                                                                      +--------------------+













=== Reading the code ===
When first reading the code, I recommend that you begin in the App class.
This this contains the main method and is the entry point of the code.
It goes through a few stages that is worthwhile to understand.
1) Parsing of options, these are saved in the Config class.
2) Register the turtle* parser
3) Register the SPARQL* query engine
4) Load data 
5) Delete Duplicates
6) Evaluation of queries

Afterwards I recommend to look at some of the test cases. They provide simple examples of the program.
These combined with the debugging tool is a nice way to gain understanding of the code.



=== Test cases ===
This project contains several test cases written in JUnit 4.
All of the tests are integration tests, (sorry, no unit tests)
These data used for the tests are contained in the project.

=== Running the software ===
The recommended way to run the software is to create a JAR and run it with the wanted set of options.
By running the JAR with the --help option, all options will be printed with an explanation.

The options are:

-h or --help    					Prints all options.
-q or --query 						This parameter should be followed by a path to a file containing a sparql* query.
-f or --query-folder				This parameter should be followed by a path to a folder containing sparql* queries (extension must be .sparqls).
-l or --location					This parameter should be followed by a path to the file containing the turtle star file, that should be loaded into the .
-p or --disable-prefix-dictionary	This parameter disables the prefix dictionary.
-i or --index 						This parameter should be followed by the type of index, valid options are: hashindex, flatindex, treeindex. (default hashindex).
-d or --dictionary 					This parameter should be followed by the type of dictionary, valid options are: InMemoryHashMap, DiskBTree. (default DiskBTree).

Only the location and query parameters are mandatory.


=== Opponents ===
Olaf and I plans to compare against two opponents.
The primary opponent is "itself".
The purpose of the project is to test the feasibility of using RDF* as a physical storage model.
Therefore, the most important experiments should show what the strengths and weaknesses of the model.
Olaf and I planned to creating a set of query templates that would show different strengths and weaknesses of the mode.
The file: queryTemplatex.txt contains the notes of these. More query templates might be needed.
There exist no instance data for the queries. It would also be good to consider the selectivity of these queries.

Because reviews might not be content with us only comparing against us self, we plan to compare again Virtuoso.
While there has been some discussions about this, there exist no concrete plans on how to do this.

=== TODO ===
This is a list of some of the TODOs for the project.
I have split the list in "essential" and "Nice to have" features

== Essential ==

Query generator
In order to make the experiments a set of queries needs to be developed.
The goal is to be have a query generator that is able to create numbers instances of query templates.

Dynamic embedded triple encoding
Currently, the encoding embedded triples is set to 20 bits for the subject, predicate, and object.
While this initially seems like a fair distribution, in practice, there are far more distinct objects than distinct predicates.
Thus, few bits are needed to encode predicates than object.
The idea is to make the distribution of bits in embedded triples customizable. 
e.g. 18 bits for subjects, 10 bits for predicates, and 32 bits for objects.
Currently the four MSB are reserved as header, but only two of them are used. These two could also be used in the distribution.


== Nice to have ==

Reuse existing database
The BTreeHybridDictionary dictionary implementation save data to disk.
However, currently the dictionary is build every time the program is run. 
It would make sense to enable the program to reuse an existing dictionary.
The library mapDB supports this feature, so it should be easily added.


Separate NodeDictionary and ReferenceDictionary
Currently the Reference Dictionary is implemented in the same class as the NodeDictionary.
It should be separated into is own class. 

Make into webservice
The idea is to make a web service that can continue running. It would than be possible to ask queries to it while it runs.



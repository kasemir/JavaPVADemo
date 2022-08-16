JavaPVADemo
===========

Demo of Java PVA library (core-pva).


EPICS Base, PVA clients, Test IOC
---------------------------------

Client demos require EPICS 7+ to run a simple test IOC:
```
softIocPVA -d db/demo.db 
```

Server demos require `pvget` etc. from EPICS 7+
as well as the PVXS `pvxget` commands for
testing `EPICS_PVA_NAME_SERVERS` and IPv7.


Building
--------

```
mvn clean verify
```

Run from within VS Code
-----------------------

In Visual Studio Code with Java support,
use File, Open Folder to open this project,
then open the `Step1...` code and "Run".


Run from Command Line
---------------------

Configure the class path that the following `java ..` commands will use:
```
export JDK_JAVA_OPTIONS="-cp target/lib/core-pva-4.6.7.jar:target/pvademo-1.0.jar"
```

Now run the examples one-by-one like this:

```
java demo.Step1Get
java demo.Step2Get
java demo.Step3Monitor
java demo.Step4Put
java demo.Step5Server
java demo.Step6Server
java demo.Step7SearchListener
```
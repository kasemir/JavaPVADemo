JavaPVADemo
===========

Demo of Java PVA library (core-pva).


Building
--------

```
mvn clean verify
```


EPICS Base, PVA clients, Test IOC
---------------------------------

Client demos require EPICS 7+ to run a simple test IOC:
```
softIocPVA -d db/demo.db 
```

Server demos require `pvget` etc. from EPICS 7+
as well as the PVXS `pvxget` commands for
testing `EPICS_PVA_NAME_SERVERS` and IPv7.


Run from within VS Code
-----------------------

In Visual Studio Code with Java support,
use File, Open Folder to open this project,
then open the `Step1...` code and "Run".


Run from Command Line
---------------------

The core-pva jar file is actually executable and offers command line client access:

```
java -jar target/lib/core-pva-4.6.7.jar 
USAGE: pvaclient info|get|monitor|put [options] <PV name>...

Options:
  -h             Help
  -w <seconds>   Wait time, default is 5.0 seconds
  -r <fields>    Field request. For 'info' command, optional field name
                 Default 'value' for 'put', empty for other operations
  -v <level>     Verbosity, level 0-5
  --             End of options (to allow '-- put some_pv -100')

For 'put', use <PV name> <value>
```


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
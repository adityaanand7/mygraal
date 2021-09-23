#!/bin/bash
rm *.class
compiler/mxbuild/linux-amd64/graaljdks/jdk11-cmp/bin/javac HelloWorld.java
compiler/mxbuild/linux-amd64/graaljdks/jdk11-cmp/bin/java -XX:+UnlockExperimentalVMOptions -XX:+UseJVMCICompiler -XX:CompileCommand=dontinline,'HelloWorld.framestatechecking' -Dgraal.Dump=:1 -Dgraal.MethodFilter='HelloWorld.framestatechecking' -Dgraal.OptDeoptimizationGrouping=false HelloWorld >/dev/null


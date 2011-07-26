#!/bin/bash

set -e

optClean=
optCleanOnly=
optStage=3
optFormat=
optUtility=
optSite=
optTests=
optTestHost=localhost
optTestPort=8080
optTestContext=aktera
optTestBrowser='*firefox /opt/firefox3/firefox-bin'
optTestCase='*'

shortOptions=c123h
longOptions=clean,format,fixversions,site,selenium,tests:,testcase:

options=("$(getopt -u -o $shortOptions --long $longOptions -- $@)")

function processOptions
{
	while [ $# -gt 0 ]
	do
		case $1 in
			-c)	optClean=1;;
			-1)	optStage=1;;
			-2)	optStage=2;;
			-3)	optStage=3;;
			--clean) optClean=1
				optCleanOnly=1;;
			--format) optFormat=1;;
			--fixversions) optUtility=fixversions ;;
			--site) optSite=1;;
			--selenium) optSelenium=1 ;;
			--tests) shift
				optTests=1
			    IFS=","
			    params=($1)
	 		    optTestHost=${params[0]}
			    optTestPort=${params[1]}
			    optTestContext=${params[2]}
			    if [ -n "${params[3]}" ]
			    then
					optTestBrowser=${params[3]}
				fi
			    ;;
			--testcase) shift
				optTestCase=$1
				;;
			-h) printf "Usage: %s: [options]\n" $0
			    printf "Options:\n"
			    printf " -c                              Perform a clean build\n"
			    printf " -1                              Start from stage 1\n"
			    printf " -2                              Start from stage 2\n"
			    printf " -3                              Start from stage 3\n"
			    printf " --format                        Format the source code\n"
			    printf " --site                          Update the project web site\n"
			    printf " --tests <host>,<port>,<context> Define the test host\n"
			    printf " -selenium                       Start the selenium server\n"
			    printf " --testcase <name>               Specifes a single test case to run\n"
			    printf " -h                              Print this help\n"
			    exit 0
			    ;;
		esac
		shift
	done
}

processOptions $options

shift $(($OPTIND - 1))

if [ ! -z "$optFormat" ]
then
	mvn java-formatter:format
	mvn license:format
	exit 0
fi

if [ ! -z "$optSite" ]
then
	mvn site:site
	mvn site:deploy
	exit 0
fi

if [ -n "$optUtility" ]
then
	case $optUtility in
		fixversions)
			mvn aktera:fixversions
			exit 0
			;;
	esac
fi

if [ -n "$optSelenium" ]
then
	mvn selenium:start-server
	exit 0
fi

if [ -n "$optTests" ]
then
	mvn integration-test -Dtest.host=$optTestHost -Dtest.port=$optTestPort -Dtest.context=$optTestContext -Dtest.case=$optTestCase
	mvn surefire-report:report-only
	mvn jxr:jxr jxr:test-jxr
	exit 0
fi

if [ "$optStage" -lt "3" ]
then
	cd ../iritgo-aktario
	if [ -n "$optClean" ]
	then
		./build.sh -c -$optStage
	else
		./build.sh -$optStage
	fi
	if [ $? != 0 ]
	then
		exit $?
	fi
	cd ../iritgo-nexim
	if [ -n "$optClean" ]
	then
		#mvn clean currently doesn't work because of dependency errors
		for i in $(find . -iname "target"); do rm -rf $i; done
	fi
	mvn install
	if [ $? != 0 ]
	then
		exit $?
	fi
	cd ../iritgo-aktera
fi

if [ -n "$optClean" ]
then
	#mvn clean currently doesn't work because of dependency errors
	for i in $(find . -iname "target"); do rm -rf $i; done
fi

mvn install -DskipITs

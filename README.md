# ssm-execute

## Introduction

This app allows users to execute concurrent commands across a fleet of AWS EC2 Instances.
   
### Prerequisites

You will need the following installed:
* Java 14
* Apache Maven 

## Building
```
git clone git@github.com:pixelcat/ssm-execute.git
cd ssm-execute
mvn clean install
```

## Running

```
java -jar target/ssm-execute-1.0-SNAPSHOT.jar <instanceIdList> <command>
```

In the above example:

|  Variable  | Description   |
|------------|---------------|
| instanceIdList | A list of ec2 instance IDs (e.g. `i-01234567890abcde`) separated by commas, no spaces |
| command | A command to execute across the instance IDs. |

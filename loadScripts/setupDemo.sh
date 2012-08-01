#!/bin/bash
WASP_HOME=/home/wasp
PROJECT_HOME=$WASP_HOME/project/wasp
CATALINA_HOME=/usr/local/tomcat/tomcat/current
echo Shutting down WASP Batch if running...
for x in `ps -ef | grep edu.yu.einstein.wasp.batch.StartSpring | grep -v grep | awk '{print $2}'`
do 
kill -9 $x
done

#echo Cleaning downloads folder...
#cd $CATALINA_HOME/downloads
#rm -rf

if [ -d $PROJECT_HOME/.git ]
then
echo "$PROJECT_HOME exists and under svn control. Going to update."
cd $PROJECT_HOME
git pull origin demo
elif [ -d $PROJECT_HOME ]
then
echo "Error: $PROJECT_HOME exists but is not under source control. Not sure what to do. Please clean up and try again"
exit 1
else
echo "$PROJECT_HOME doesn't exist. Going to create and pull demo branch from git."
mkdir -p $WASP_HOME/project
cd $WASP_HOME/project
git clone http://frankfurt.aecom.yu.edu/git/wasp/blessedRepository.git wasp
cd $PROJECT_HOME
git checkout --track remotes/origin/demo
fi

echo "Updating demo branch from git repository..."
cd $PROJECT_HOME
git pull origin demo
echo "Restoring wasp database to demo default..."
cd $PROJECT_HOME/db
mysql -u wasp --password=waspV2 -D wasp < $PROJECT_HOME/db/waspDemoDb.sql

echo Updating and re-deploying WASP System...
$WASP_HOME/bin/deployWasp.sh

cd $PROJECT_HOME/wasp-web
# redeploy as sometimes doesn't load the first time (Tomcat 7 bug)
mvn tomcat:redeploy

echo Starting batch...
cd $PROJECT_HOME/wasp-exec
nohup java -Dcatalina.home=$CATALINA_HOME -cp "target/wasp-exec-0.1.0-SNAPSHOT.jar:$CATALINA_HOME/waspPlugins/*" edu.yu.einstein.wasp.batch.StartSpring > /dev/null 2>&1 &

echo Re-building and deploying documentation..
$WASP_HOME/bin/deployDocumentation.sh

echo done.
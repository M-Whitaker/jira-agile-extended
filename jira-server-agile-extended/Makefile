.PHONY: jira debug test logs clean

jira:
	atlas-debug --product jira

debug: target/jira/webapp/WEB-INF/classes/log4j.properties
	echo "\nlog4j.logger.uk.co.mattwhitaker = TRACE, filelog" >> target/jira/webapp/WEB-INF/classes/log4j.properties

test:
	atlas-integration-test --product jira

logs:
	tail -f target/jira/home/log/atlassian-jira.log | grep "u.c.m.a.jiraserveragileextended"

clean:
	atlas-clean

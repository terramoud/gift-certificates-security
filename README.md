## Module #6. CI/CD

<article class="markdown-body entry-content container-lg" itemprop="text">

<h4 dir="auto"><a id="user-content-application-requirements" class="anchor" aria-hidden="true" href="#application-requirements"><svg class="octicon octicon-link" viewBox="0 0 16 16" version="1.1" width="16" height="16" aria-hidden="true"><path d="m7.775 3.275 1.25-1.25a3.5 3.5 0 1 1 4.95 4.95l-2.5 2.5a3.5 3.5 0 0 1-4.95 0 .751.751 0 0 1 .018-1.042.751.751 0 0 1 1.042-.018 1.998 1.998 0 0 0 2.83 0l2.5-2.5a2.002 2.002 0 0 0-2.83-2.83l-1.25 1.25a.751.751 0 0 1-1.042-.018.751.751 0 0 1-.018-1.042Zm-4.69 9.64a1.998 1.998 0 0 0 2.83 0l1.25-1.25a.751.751 0 0 1 1.042.018.751.751 0 0 1 .018 1.042l-1.25 1.25a3.5 3.5 0 1 1-4.95-4.95l2.5-2.5a3.5 3.5 0 0 1 4.95 0 .751.751 0 0 1-.018 1.042.751.751 0 0 1-1.042.018 1.998 1.998 0 0 0-2.83 0l-2.5 2.5a1.998 1.998 0 0 0 0 2.83Z"></path></svg></a>Task requirements</h4>
<ol dir="auto">
<li>Configure Jenkins security (install Role strategy plugin). Remove anonymous access. Create administrator user (all permissions) and developer user (build job, cancel builds). Add Jenkins credentials to Readme file in your git repository.</li>
<li>Configure Jenkins build job (pool, run test, build) to checkout your repository, use pooling interval.</li>
<li>Install SonarQube. Configure Jenkins to use local SonarQube installation. Analyze your source code with SonarQube after Maven builds your project. Use JaCoCo for code coverage.</li>
<li>Jenkins should deploy your application (after passing SonarQube quality gate) under your local tomcat server. Please use Jenkins Tomcat Deploy plugin.</li>
</ol>
</article>

## Jenkins Credentials

- Administrator user:
  - Username: administrator
  - Password: ********

- Developer user:
  - Username: developer
  - Password: ******** 
  
- Tomcat's user: 
  - Username: admin
  - Password: ********  
  
- Sonar's user:
  - Username: admin
  - Password: ********
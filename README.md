# activiti-cloud-sfdc-integration-example

![Process Diagram](process-diagram.png)

## Process Flow

1. When new Campaigns are Created in SFDC, Events emitted using SFDC PushTopic 
2. Activiti Cloud Connector (activiti-cloud-connector-sfdc-event-listener) listens to events and kicks off Campaign Approval Process (in activiti-runtime-bundle-campaign-management component) shown above 
3. Interaction with SFDC at various points (service tasks) of the process via another Activiti Cloud Connector (activiti-cloud-connector-sfdc-rest) 
4. If the Campaign is approved, the process will activate the Campaign in SFDC & set the status to In-Progress on Campaign Start Date, else Abort it!

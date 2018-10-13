# TrackAlertApp

Indian Railways has a huge system of trains running throughout the country on a daily basis. For any large system, the safety of their workers and passengers is of utmost importance. Gangmen are people whose job is to find any inconsistency or fault in the railway tracks. Their daily job is very risky as they have to conduct their work between the regular schedule of the trains. 

A gangman’s job is a very accident prone occupation as there are many instances of accidents related to gangmen. A young railway gangaman died after he was knocked down by a train near Kalnad bridge in Kerala (“Railway Gangman Killed”, 2016). Another incident which took place in Varanasi when a railway gangman came under the wheels of a train (“Railway Gangman Dies”, 2014). A gangman accident even took place in Mumbai where a 27-year-old was killed while walking on the tracks (“Railway Gangman Hit”, 2016). All these incidents point to the fact that railway gangmen are at a constant life threat and the seriousness of this problem is rising day by day.

As the frequency of these accidents was increasing, the Indian Railways started implanting a technology developed by Research Designs Standards Organization (RDSO) called Train Collision Avoidance System (TCAS) to prevent such accidents. This technology will generate derailment alerts and apply emergency brakes in critical situations which will control human errors. This technology will also help gangmen as the trains will apply breaks automatically as soon as it detects a chance of collision (Shukla, 2014). 

This was the only technology mentioned to help with the major safety issue related to gangmen but this technology was basically designed for train drivers and provides no information or alerts to gangmen while they are working. Additionally, this technology costs around Rs 22 crore which isn’t the most feasible solution to a problem like this. The main shortcoming of this technology was that the gangmen are still left in the dark as they aren’t getting an alert system of their own which warns them about the incoming train. 

Gangman Alert is a new idea which focuses on removing the shortcomings of the existing technology. An app was the best fit to use for this problem as everyone carries a smartphone these days making the cost of implementation virtually nothing. Also, in the past android apps have helped deal with various difficult situations such as safety of women (“7 Best Women Safety”). Nowadays mobile apps are used to solve all kinds of problems like for transportation apps like OLA, Uber are household names. Ordering food online can be done efficiently too using apps like Food Panda and Swiggy. Mobile applications are a global phenomenon and people are developing new mobile apps constantly to solve every small or big problem around them efficiently (Adeleye, 2016). Every gangman working on the tracks will have this app which will alert them to the incoming trains on the track they are working on. 

The app which is primarily designed for gangmen working on the tracks uses an existing service called the TMS service which provides data about the trains and the track names on which the trains are running. This app will help the gangmen as it will alert them while they are working if any train is coming on their track or not. 

# Objectives 

1.	Alert the gangman about the trains coming on the tracks they are working on.
2.	No implementation cost as everyone owns a smartphone these days

# Basic Architecture

![figure 1](https://github.com/anarghya-das/TrackAlertApp/blob/master/Images/fig1.png)<br>
*Figure 1: Basic Architecture Diagram*<br>

<b>TMS Server</b>: The main server on which the mobile application works. It is responsible to provide the train information in the form of JSON. The server updates the JSON data about every 5-10 millisecond. 
```JSON
[{
	"direction":"String content",
	"location":"String content",
	"locoNo":"String content",
	"trackId":2147483647,
	"trackName":"String content",
	"trainId":9223372036854775807,
	"trainName":"String content",
	"trainNo":2147483647,
	"updatedAt":"String content",
	"zSignals":[{
		"distance":1.26743233E+15,
		"index":2147483647,
		"station":"String content",
		"trackName":"String content",
		"ztoAspectSignal":{
			"objectName":"String content",
			"objectType":"String content",
			"relays":[{
				"channelDescription":"String content",
				"currentStatus":"String content"
			}]
		},
		"ztoCallingOnSignal":{
			"objectName":"String content",
			"objectType":"String content",
			"relays":[{
				"channelDescription":"String content",
				"currentStatus":"String content"
			}]
		},
		"ztoShuntSignal":{
			"objectName":"String content",
			"objectType":"String content",
			"relays":[{
				"channelDescription":"String content",
				"currentStatus":"String content"
			}]
		}
	}]
}]                                     
```
*Figure 2: Sample TMS Server JSON data* <br>

The text shown in Figure 2 is a sample JSON data sent by the TMS server. Direction, Track Name, Train ID, Train Name, Train Number, Track Name are the information extracted from the JSON data provided by the TMS Server. Train objects are created using this information.

<b>Mobile Application: </b> Sends and receives the HTTP Requests and performs all the processes necessary for the working of the application.

# Use Case Diagram

**Gangman**: The primary actor of the Use Case diagram who interacts with the Main Screen and Alert View of the app. The app was primarily designed for the primary actor, Gangman. 

**TMS**: The secondary actor of the Use Case diagram which provides the app with the train data from which the required information is fetched from the app.

**Main Screen**: The first module with which the primary actor (gangman) interacts with as soon as the app is opened. The main screen has an “include” relationship with **Connectivity Check** which means that every time Main Screen module starts the connectivity check for the internet connection is bound to happen. The main screen also has an “extend” relationship with **Display Error** which means that it will not be shown every time the Main Screen module starts but only under certain conditions (when connectivity check fails). 

**Alert View**: The second module with which the primary actor (gangman) can interact after the main screen module. It has an “include” relationship with **Train Data** which means that every time this module starts the app will fetch the train data from the TMS server automatically. It also has an “extend” relationship with **Connection Lost Error** which means that it will not be shown every time this module starts but only under certain conditions (when an internet connection is lost). 

![figure 3](https://github.com/anarghya-das/TrackAlertApp/blob/master/Images/fig3.png)<br>
*Figure 3: Use Case Diagram*

# Mobile Application Design

The app design was divided into two modules: MainScreenActivity where the gangman enters the name of the track he is currently working on and MainActivity where the alert notification is displayed if a train is approaching the track on which the gangman is working. 

![figure 4](https://github.com/anarghya-das/TrackAlertApp/blob/master/Images/fig4.png)<br>
*Figure 4: Mobile Application Diagram<br>

1.	**Background Process**: This is a process which takes place in the background while the app is running. There are three main jobs that this process handles:<br>
a.	To create HTTP connections to the server and receive the response from the server. <br>
b.	To update the UI accordingly after it has received a response from the server. <br>
c.	To check the network connectivity of the application.<br>
This mobile application uses the HTTP POST method to connect to the TMS Server. In this type of connection, data is usually required to be sent to the server and then a response is received accordingly. It also uses HTTP GET method to connect to the database server. In this type of connection, data is not required to be sent to the server and response if received accordingly.

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
*Figure 4: Mobile Application Diagram*<br>

1.	**Background Process**: This is a process which takes place in the background while the app is running. There are three main jobs that this process handles:<br>
a.	To create HTTP connections to the server and receive the response from the server. <br>
b.	To update the UI accordingly after it has received a response from the server. <br>
c.	To check the network connectivity of the application.<br>
This mobile application uses the HTTP POST method to connect to the TMS Server. In this type of connection, data is usually required to be sent to the server and then a response is received accordingly. It also uses HTTP GET method to connect to the database server. In this type of connection, data is not required to be sent to the server and response if received accordingly.

2.	**MainScreenActivity Module**
This module represents the welcome screen of the app where the gangman entered track name(s) are transferred to the MainActivity module using the **startApplication** function. This module also checks whether the gangman is connected to the internet via mobile data or WiFi using the **connectivityCheck** function.

3.	**MainActivity Module** 
As soon as this activity starts, the volume of the phone is set to maximum using the **increaseVolumeToMax** function, the display timeout of the phone is turned off while on this module and HTTP request is sent to the TMS Server in the **background process**. The response which is a JSON string containing all the train information is parsed and created into Train objects. Out of all the train objects, the track names are extracted and stored separately. Then the track names entered by the user are matched with the current list of all the track names obtained from the server. If the track names match, a positive response is sent from the **background process** to the MainActivity along with those respective track names which matched. A siren picture is flashed on the screen and a siren noise is started using the **BackgrounProcessFinish** function. If the track names do not match, a negative response is sent from the **background process** to the MainActivity. A proper text message is displayed accordingly using the background process. This module can be closed using the **Stop** function which will stop the background alert sound (if playing) and bring the app back to the main screen. 

# Devlopment 

The app was developed on android because android devices are most popular in India and because of it is open source there was the low cost of development. The IDE used in the development of this app was Android Studio. 
**Async Task** was used as a **background process** because it fulfills all the three objectives required from the **background process**. Async Task has a function called **doInBackground** where the HTTP requests are made and the adequate response is then transferred to another method inside the **Async Task** called the **onPostExecute** which updates the UI based on the response.
**Handler** and **Timer** were used in combination with the **Async Task** to make the HTTP requests to the server in every 5 milliseconds. The 5 millisecond time was chosen because of the TMS server updates with new data every 5-10 millisecond, therefore to avoid losing any data the time minimum interval of 5 milliseconds was chosen. The mobile application uses siren alert to inform the gangman about the arriving train which also works when the phone’s screen is off or the app is running in the background. This was also done with the help of **Async Task**. 
This project also has two dependencies namely a JSON library which is used to parse the JSON responses received by the app and Android support design libraries to bring the material look and feel to the app.

# Usage

The gangmen are allotted their track names beforehand. When the app starts, the gangman is greeted with a welcome screen where the gangman is required to input the track name or multiple track names. As soon as the track name(s) are entered the gangman can click on start to proceed to the next screen of the app. Figure 5 shows the state of the Main Screen after the gangman has entered the track name.

![figure 5](https://github.com/anarghya-das/TrackAlertApp/blob/master/Images/fig5.jpg)<br>
*Figure 5: Screenshot of Main Screen*<br>

The next screen of the app shows the alert screen. If there is a train incoming on the track(s) entered by the gangman, then the screen will display the track names on which the train is coming along with a flashing siren image and a siren noise will start playing in the background (*Figure 6*).  Otherwise, if there is no train incoming on the track entered by the gangman, then the screen will display a normal text message (*Figure 7*). The siren audio can be muted and the alert screen can be stopped using the end button.                                          


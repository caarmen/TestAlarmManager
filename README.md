Demonstration of Alarm Manager bug on Samsung Galaxy S6
=======================================================

Summary:
--------
Alarms are not scheduled at the proper time in the following cumulative conditions:
* The alarm is scheduled in the future at a multiple of 5 minutes plus or minus 5 seconds
AND
* The applicationId does NOT contain the word "alarm"
AND
* The application creates an account on the device

If any one of these conditions is not true, the alarm is scheduled correctly.


Steps to reproduce:
-------------------
* Modify build.gradle: change the applicationId to anything that does NOT contain "alarm".
* Build this app:
```
./gradle clean assembleDebug 
```
* Install the app to an S6 device
* Set the "Alarm delay" to a multiple of 5 minutes, plus or minus 5 seconds.  For example: 600 seconds.
* Tap the enable switch to schedule the alarm
* Run the following command to see at what time the alarm is really scheduled:
```
adb shell dumpsys alarm -B1 -A4 | egrep tag.*carmen
```
* You will notice that the alarm was not scheduled at the expected time.
* Tap the switch to disable the alarm
* Run the dumpsys alarm command again
* You will not see the alarm, which is correct.
* Tap the switch to enable the alarm again
* Run the dumpsys alarm command again
* You will notice that the alarm was once again incorrectly scheduled. Furthermore, you can see that it was scheduled at the same time the previous alarm was scheduled.


Any one of the following changes will result in the alarm being properly scheduled:
* Change the delay of the alarm to a value which isn't a multiple of 5 minutes plus or minus 5 seconds.  Example: 403 seconds.
* In build.gradle, make sure the applicationId contains the word "alarm"
* In AlarmManagerTester.onCreate, comment out this line:
```
// AccountUtils.createAccount(this);
```



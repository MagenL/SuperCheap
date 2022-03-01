# SuperCheap - The supermarket price comparison APP

This project is an Android application that compares supermarket prices via an API published by the Consumer Protection and FairTrade Authority of Israel.
 

## Description

This application retrieve data from 4 big supermarket brands in Israel - Shufersal, Mahsani Ashok, Super Bareket and Victory.
In the app you can do the following :


**Screenshots**

 <table>
        <tbody>
        <tr>
        <td><img src="images/login screen.jpeg" alt=""></td>
        <td><img src="images/search for super in dashboard screen modi'in.jpeg" alt=""></td>
        <td><img src="images/dashboard screen after adding supers.jpeg" alt=""></td>
        <td><img src="images/entering a super after find the cheapest one from the comparing screen.jpeg" alt=""></td>
        </tr>
        <tr>
         <td><img src="images/comparing list 1.jpeg" alt=""></td>
         <td><img src="images/comparing list with brand.jpeg" alt=""></td>
         <td><img src="images/online carts search for uploader before clicking.jpeg" alt=""></td>
         <td><img src="images/online carts.jpeg" alt=""></td>
        </tr>
        </tbody>
    </table>



### `Add supermarkets database to your device`

This feature allows you to add supers all around Israel to your device.


### `Search for products and create list of products in a specific super`

This feature allows you to search and create lists of products in a specific super.
the result comes from a real-time and up-to-date database from the very super you have selected.


### `Find the cheapest super`

With this feature you can search for products and create list of products.

as a result you get list of supers that provides all your product with a price comparison.
afterwards you can easily choose the cheapest super

### `Upload a list to the cloud`

**Note: this is a one-way operation. Once you `upload list`, you can't go back!**

Uploading a list is an optional operation.
This feature allows you to share your list by uploading it to the application server side **'hosted by firebase real-time-database'**.
After your list is uploaded successfully it can be found via the 'online cart' screen with the 'search' button by typing your name.



## Technologies and libraries

**Python 3**

**Web scraping**

In this section I used Python libraries such as **request and BeautifulSoup**.
these libraries used to download website's html documents with the additional data needed to manage each database of the supermakets of each brand.


**Download and destructuring data from the API**

The API I used in this project provides html pages with download links to xml files locked in zip.
To manage to unlock each file from the zip, read the data from it and destruct it.
to readable data I used Python libraries such as **urllib, xmltodict, gzip, json** and more.


**Chaquopy**

Chaquopy provides everything you need to include Python components in an Android app.

This library provides simple calling Python code from Java/Kotlin, and vice versa.



**MVVM Architecture**

**ViewModel**

* The ViewModel class is designed to store and manage UI-related data in a lifecycle conscious way.\n
The ViewModel class allows data to survive configuration changes such as screen rotations.

In the viewmodel I retrieve the data from the python scripts which used as a repository-service for the API calls.\
The retrieved data I stored in **Room database** and display it to the user with **LiveData observer**

**Room Database**
* Google's library which use SQLite to store data in SQL architecture.\

* All the downloaded data from the Consumer Protection and FairTrade Authority of Israel API stored in room database.\
This way, the application can use the downloaded data without an Internet connection.

*If the user's database is older that 24h, the application suggest the user to update the database.

**LiveData**

*LiveData is an observable data holder class.

* Displaying the supermarkets database from Room database with livedata.

* while the application is loading data or/and downloading data, the application shows the progress by UI dialog that triggered by livedata of loading proccess.

**Coroutines and Threads**

* Using coroutine threads to dispatch proccesses such as downloading data and retrieve stored data

**Picasso**
* Downloading images and present it to the UI with Picasso library.

**SharedPreferences**
* Storage and mangement user flags data.


### Author
Magen Levi

magen.levi5@gmail.com

Israel





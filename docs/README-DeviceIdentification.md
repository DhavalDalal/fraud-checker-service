# Device Identification
_(Excerpt From [https://www.techtarget.com/searchcio/definition/device-ID-device-identification](https://www.techtarget.com/searchcio/definition/device-ID-device-identification))_

A device ID (device identification) is an anonymous string of numbers and letters that uniquely identifies a mobile device such as a smartphone, tablet or smartwatch.

Stored inside a mobile device, it's typically used to refer to a device's model. Mobile apps and programs frequently retrieve the device ID while interacting with servers to identify themselves.

Device IDs play an important role in unique device identification. The following are some use cases of device IDs:

1. **Targeted marketing**: A device ID can be used by marketers, advertisers and app owners to identify mobile users. It reveals specific user behavior and patterns, enabling them to group users into cohorts based on factors such as device or location. In essence, a device ID gives a far clearer picture of user behavior, which helps with targeted content marketing and conversions. For example, personalized ads can be delivered to an app user based on their preferences.
2. **Enhanced user experience**: Device IDs give app developers the ability to monitor which mobile devices have their apps installed, enabling them to provide users with a more tailored and effective user experience. By understanding the user's application preferences, they can gauge what features of the app are working and what issues need to be resolved.
3. **Tracking of installs and sales**: Apps and mobile measurement partners (MMPs) occasionally use device IDs to track and record attribution events such as app installs and sales.
4. **Understanding user interaction**: By gathering in-app event data, device IDs enable app owners to have a better understanding of user interaction and their customer journey. They may then determine if consumers drop off or churn or move farther down the funnel and develop into devoted customers by identifying when, where and why they interact the way they do.
5. **Assessing Risk of a Transaction**:

## Server-Side Vs. Client-Side Detection 
(Excerpt From: [https://www.smashingmagazine.com/2012/09/server-side-device-detection-history-benefits-how-to/](https://www.smashingmagazine.com/2012/09/server-side-device-detection-history-benefits-how-to/))

Many client-side JavaScript libraries have recently been released that enable developers to determine certain properties of the browser with a simple JavaScript API. The best known of these libraries is undoubtedly Modernizr. These feature tests are often coupled with “polyfills” that replace missing browser capabilities with JavaScript substitutes. These clients-side feature-detection libraries are very useful and are often combined with server-side techniques to give the best of both worlds, but there are limitations and differences in approach that limit their usefulness:

* They detect only browser features and cannot determine the physical nature of the underlying device. In many cases, browser features are all that is required, but if, for example, you wish to supply a deep link to an app download for a particular Android OS version, a feature detection library typically cannot tell you what you need to know.
* The browser features are available only after the DOM has loaded and the tests have run, by which time it is too late to make major changes to the page’s content. For this reason, client-side detection is mostly used to tweak visual layouts, rather than make substantive changes to content and interactions. That said, the features determined via client-side detection can be stored in a cookie and used on subsequent pages to make more substantive changes.
* While some browser properties can be queried via JavaScript, many browsers still return false positives for certain tests, causing incorrect decisions to be made.
* Some properties are not available at all via Javascript; for example, whether a device is a phone, tablet or desktop, its model name, vendor name and maximum HTML size, whether it supports click-to-call, and so on.
* While server-side detection does impose some load on the server, it is typically negligible compared to the cost of serving the page and its resources. Locally deployed server-side solutions can typically manage well in excess of tens of thousands of recognitions per second, and far higher for Apache and NGINX module variants that are based on C++. Cloud-based solutions make a query over the network for each new device they see, but they cache the resulting information for a configurable period afterward to reduce latency and network overhead.

The main disadvantages of server-side detection are the following:

* The device databases that they utilize need to be updated frequently. Vendors usually make this as easy as possible by providing sample cron scripts to download daily updates.
* Device databases might get out of date or contain bad information causing wrong data to be delivered to specific range of devices.
* Device-detection solutions are typically commercial products that must be licensed.
* Not all solutions allow for personalization of the data stored for each device.

The first point to note is that not all devices support modern HTML5 and JavaScript features equally, so some of the problems from the early days of the mobile Web still exist. Client-side feature detection can help work around this issue, but many browsers still return false positives for such tests.
Device detection might seem to cost significantly more than responsive design, but device detection is what gives a company control. When the business model changes, device detection enables a company to shuffle quite a few things around and still avoid panic mode. One way to look at it is that device detection is one application of the popular divide-and-conquer strategy. On the other hand, Responsive Web Designer might be very limiting (particularly if the designer who has mastered all of that magic happens to have changed jobs).

While many designers embrace the flexible nature of the Web, with device detection, you can fine-tune the experience to exactly match the requirements of the user and the device they are using. This is often the main argument for device detection — it enables you to deliver a small contained experience to feature phones, a rich JavaScript-enhanced solution to smartphones and a lean-back experience to TVs, all from the same URL.

## The History Of Sever-Side Device Detection
_(Excerpt From: [https://www.smashingmagazine.com/2012/09/server-side-device-detection-history-benefits-how-to/](https://www.smashingmagazine.com/2012/09/server-side-device-detection-history-benefits-how-to/))_

First-generation mobile devices had no DOM and little or no CSS, JavaScript or ability to reflow content. In fact, the phone browsers of the early 2000s were so limited that the maximum HTML sizes that many of them handled was well under 10 KB; many didn’t support images, and so on. Pages that didn’t cater to a device’s particular capabilities would often cause the browser or even the entire phone to crash. In this very limited environment, server-side device detection (“device detection” henceforth) was literally the only way to safely publish mobile Web content, because each device had restrictions and bugs that had to be worked around. Thus, from the very earliest days of the mobile Web, device detection was a vital part of any developer’s toolkit.

With device detection, the HTTP headers that browsers send as part of every request they make are examined and are usually sufficient to uniquely identify the browser or model and, hence, its properties. The most important HTTP header used for this purpose is the user-agent header. The designers of the HTTP protocol anticipated the need to serve content to user agents with different capabilities and specifically set up the user-agent header as a means to do this; namely, RFC 1945 (HTTP 1.0) and RFC 2616 (HTTP 1.1). Device-detection solutions use various pattern-matching techniques to map these headers to data stores of devices and properties.

With the advent of smartphones, a few things have changed, and many of the device limitations described above have been overcome. This has allowed developers to take shortcuts and create full-fledged websites that partially adapt themselves to mobile devices, using client-side adaptation.

This has sparked the idea that client-side adaptation could ultimately make device detection unnecessary. The concept of a “one-size-fits-all” website is very romantic and seductive, thanks to the potential of JavaScript to make the device-fragmentation problem disappear. The prospect of not having to invest in a device-detection framework makes client-side adaptation appealing to CFOs also. However, we strongly believe that reality is not quite that simple.

## The Technicalities

### User Agent

_Excerpt from: [https://deviceatlas.com/blog/user-agent-parsing-how-it-works-and-how-it-can-be-used](https://deviceatlas.com/blog/user-agent-parsing-how-it-works-and-how-it-can-be-used)_
A User-Agent (UA) is an alphanumeric string that identifies the ‘agent’ or program making a request to a web server for an asset such as a document, image or web page. It is a standard part of web architecture and is passed by all web requests in the HTTP headers. The User-Agent string is very useful because it tells you quite specific information about the software and hardware running on the device that is making the request. You can make important decisions on how to handle web traffic based on the User-Agent string, ranging from simple segmentation and redirection, to more complex content adaptation and device targeting decisions.

Although the User-Agent doesn't identify specific individuals, it does provide developers with an extremely powerful method of analysing and segmenting traffic. This information, gleaned directly from the User-Agent string itself (a process known as User-Agent parsing) typically includes browser, web rendering engine, operating system and device. Deeper information can be returned when the User-Agent string is mapped to an additional set of data about the underlying device.

_Excerpt from: [https://techlab.bol.com/en/blog/making-sense-user-agent-string/](https://techlab.bol.com/en/blog/making-sense-user-agent-string/)_

When a web browser does an HTTP request to a website, one of the headers in such a request is the User-Agent header as described in ```RFC 2616```:

The ```User-Agent``` request-header field contains information about the user agent originating the request. This is for statistical purposes, the tracing of protocol violations, and automated recognition of user agents for the sake of tailoring responses to avoid particular user agent limitations.

An example of a user agent is this:

```
Mozilla/5.0 (Android 4.4; Tablet; rv:41.0) Gecko/41.0 Firefox/41.0
```

It clearly indicates the type and version of the operating system, the device type and the browser name and version. Nice!

For further reading Click [here](MakingSenseOfTheUserAgentString-Techlab.pdf)
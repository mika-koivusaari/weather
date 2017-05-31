/**
 * 
 */

  var functionList=[];
      
  //Add callback to timers list.
  function addFunction(func,interval) {
    var callable={};
    callable.function=func;
    callable.interval=interval;
    callable.counter=interval;
    functionList.push(callable);
  }
            
  //Decrease every callbacks counter every minute
  function timer() {
    for (var key in functionList) {
      var callable=functionList[key];
      callable.counter--;
    }
    callTimerCallbacks();
  }

  //Call every callback that has counte equal or less than 0.
  function callTimerCallbacks() {
//    console.log("callTimerCallbacks "+document[hidden]);
    if (!document[hidden]) {
      for (var key in functionList) {
        var callable=functionList[key];
        if (callable.counter<=0) {
          callable.function();
          callable.counter=callable.interval;
        }
      }
    }
  }

  // Set the name of the hidden property and the change event for visibility
  var hidden, visibilityChange; 
  if (typeof document.hidden !== "undefined") { // Opera 12.10 and Firefox 18 and later support 
    hidden = "hidden";
    visibilityChange = "visibilitychange";
  } else if (typeof document.msHidden !== "undefined") {
    hidden = "msHidden";
    visibilityChange = "msvisibilitychange";
  } else if (typeof document.webkitHidden !== "undefined") {
    hidden = "webkitHidden";
    visibilityChange = "webkitvisibilitychange";
  }
 
  // Warn if the browser doesn't support addEventListener or the Page Visibility API
  if (typeof document.addEventListener === "undefined" || typeof document[hidden] === "undefined") {
    console.log("This demo requires a browser, such as Google Chrome or Firefox, that supports the Page Visibility API.");
  } else {
    // Handle page visibility change   
    document.addEventListener(visibilityChange, callTimerCallbacks, false);
  }
    
  //main timer that's launched every minute
  var mainTimer = setInterval(timer, 60000);

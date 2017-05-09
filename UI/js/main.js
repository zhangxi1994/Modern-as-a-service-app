
//globals

jwt_token = null; //recived from server, passed back doing payment

cart_items = [];

items_id = [];

rec_i = [];

total_price = 0;

var accountDisplayHandler = {
    userName: null,
    loginNavElement: $("#loginNavElement"),
    logoutNavElement: $("#logoutNavElement"),
    signUpNavElement: $("#signUpNavElement"),
    usernameNavElement: $("#usernameNavElement"),
    ordersInfoNavElement: $("#ordersInfoNavElement"),
    cartNavElement: $("#cartNavElement")
};

var handler = StripeCheckout.configure({
  key: 'pk_test_zsMITwkFAOrv7IiPAY2jCm11',
  image: 'https://stripe.com/img/documentation/checkout/marketplace.png',
  locale: 'auto',
  token: function(token) {
    cleanData = {};
    cleanData['resource'] = "order";
    cleanData['stripeToken'] = token.id;
    cleanData['stripeEmail'] = accountDisplayHandler.userName;
    console.log(cleanData);
    finish = 0;
    while (!finish){
      $.ajax({
          type: "POST",
          url: 'https://dh0y47otf3.execute-api.us-west-2.amazonaws.com/prod/customer/orderpayment',
          crossDomain: true,
          contentType: 'application/json',
          data: JSON.stringify(cleanData),
          dataType: 'json',
          success: function(service_data) {
             // accountDisplayHandler.cartNavElement.hide();
             if (service_data['result']==""){
                innerHTML = "Please wait while the payment is processing ...";
                $("#cartContent").html(innerHTML);
             } else {
                innerHTML = "Payment completed! Thanks for you support.";
                $("#cartContent").html(innerHTML);
                finish = 1;
             }
          },
          error: function (e) {
             alert("Unable to purchase.");
             finish = 1;
          }
      });
    }
  }
});

accountDisplayHandler.logOut = function () {
    this.userName = null;
    this.usernameNavElement.html("")
    this.usernameNavElement.hide();
    this.logoutNavElement.hide();
    this.signUpNavElement.show();
    this.loginNavElement.show();

    this.ordersInfoNavElement.hide();
    this.cartNavElement.hide();

    jwt_token = "";
}


accountDisplayHandler.logIn = function (userName) {
    console.log("hit form handler");
    this.userName = userName;
    //this.usernameNavElement.html("<a href='#'>" + userName + "</a>");
    this.usernameNavElement.html("<a href=\"#\" data-toggle=\"modal\" data-target=\"#userInfoModal\">" + userName + "</a>");
    this.usernameNavElement.show();
    this.signUpNavElement.hide();
    this.loginNavElement.hide();
    this.logoutNavElement.show();

    this.ordersInfoNavElement.html("<a href=\"#\" data-toggle=\"modal\" data-target=\"#ordersInfoModal\">" + "Orders" + "</a>");
    this.ordersInfoNavElement.show();
    this.cartNavElement.html("<a href=\"#\" data-toggle=\"modal\" data-target=\"#cartInfoModal\">" + "Cart" + "</a>");
    this.cartNavElement.show();
}

function setCookie(key, value) {
    var expires = new Date();
    expires.setTime(expires.getTime() + (1 * 24 * 60 * 60 * 1000));
    //jwt_token = key + "=" + value + ";expires=" + expires.toUTCString();
    jwt_token = value;
    //for single page: jwt_token, for multi page: document.cookie
}

function getCookie(key) {
    // var name = key + "=";
    // //var decodedCookie = decodeURIComponent(document.cookie);
    // var ca = jwt_token.split(';');
    // for(var i = 0; i < ca.length; i++) {
    //     var c = ca[i];
    //     while (c.charAt(0) == ' ') {
    //         c = c.substring(1);
    //     }
    //     if (c.indexOf(name) == 0) {
    //         return c.substring(name.length, c.length);
    //     }
    // }
    return jwt_token;
}

function render_items () {
    var itemLength=0;

    cleanData = {};
    cleanData['resource'] = "allitems";
    cleanData['type'] = "ListAllItems";
    $.ajax({
        type: "POST",
        url: 'https://dh0y47otf3.execute-api.us-west-2.amazonaws.com/prod/allitems',
        crossDomain: true,
        contentType: 'application/json',
        data: JSON.stringify(cleanData),
        dataType: 'json',
        success: function(service_data){
           // console.log(service_data);
           items_data = service_data['items'];

           // console.log(items_data);
           for(var item in items_data){
                itemLength++;
           }
           // console.log(itemLength);

           innerHTML = "";
           for(var i=0;i<itemLength;i++){
                items_id.push(items_data[i]['id']);
                if (i%3==0){
                    innerHTML = innerHTML + "<div class=\"row\">";

                }

                innerHTML = innerHTML + "<div class=\"col-sm-6 col-md-4\">";
                innerHTML = innerHTML + "<div class=\"thumbnail\">";
                innerHTML = innerHTML + "<img src=\"";
                innerHTML = innerHTML + "images/" + items_data[i]['id'] + ".png\" " + "alt=\"" + items_data[i]['item_name'] + "\">";
                innerHTML = innerHTML + "<div class=\"caption\">";
                innerHTML = innerHTML + "<h3>" + items_data[i]['item_name'] + "</h3>";
                innerHTML = innerHTML + "<p>" + items_data[i]['description'] + "</p>";
                innerHTML = innerHTML + "<button type=\"button\" class=\"btn btn-default\" id=\"item" + i + "\" value=\"" + i +"\">Add to Cart</button>";
                innerHTML = innerHTML + "</div></div></div>";

                if (i%3==2){
                    innerHTML = innerHTML + "<div class=\"row\">";
                }
           }
           if (itemLength%3!=2){
                innerHTML = innerHTML + "<div class=\"row\">";
           }
           // console.log(innerHTML);
           $("#container").html(innerHTML);
           // console.log(itemLength);
           for (var i=0;i<itemLength;i++){
                target_string = "#item" + i;
                // console.log(target_string);
                // rec_i.push(i);
                // console.log(rec_i[i]);
                $( target_string ).click(function() {
                    // console.log(items_id[i]);
                    // console.log("itemLength: " + i);
                    addCart();
                    
                });
            }
        },
        error: function (e) {
           alert("Unable to retrieve your data.");
        }
    });
}

accountDisplayHandler.userInfo = function() {
    console.log(jwt_token);

    cleanData = {};
    cleanData['resource'] = 'customer';
    cleanData['type'] = 'CustomerInfo';
    cleanData['jwt'] = jwt_token;
    $.ajax({
        type: "POST",
        url: 'https://dh0y47otf3.execute-api.us-west-2.amazonaws.com/prod/customer/info',
        crossDomain: true,
        contentType: 'application/json',
        data: JSON.stringify(cleanData),
        dataType: 'json',
        success: function(service_data){
           if (service_data['status']=='success'){
               customer_data = service_data['customer']
               insertHTML = "";
               insertHTML = insertHTML + "<table class=\"table table-hover\">";
               insertHTML = insertHTML + "<tbody>";
               insertHTML = insertHTML + "<tr>" + "<td>ID</td><td>" + customer_data['id']+ "</td></tr>";
               insertHTML = insertHTML + "<tr>" + "<td>First Name</td><td>" + customer_data['first_name']+ "</td></tr>";
               insertHTML = insertHTML + "<tr>" + "<td>Last Name</td><td>" + customer_data['last_name']+ "</td></tr>";
               insertHTML = insertHTML + "<tr>" + "<td>Date of Birth</td><td>" + customer_data['date_of_birth']+ "</td></tr>";
               insertHTML = insertHTML + "<tr>" + "<td>Balance</td><td>" + customer_data['balance']+ "</td></tr>";
               insertHTML = insertHTML + "</tbody>";
               insertHTML = insertHTML + "</table>";
               $("#userContent").html(insertHTML);
           }
           else{
               alert("No accessibility.");
           }
        },
        error: function (e) {
           alert("Unable to retrieve your data.");
        }
    });
}

accountDisplayHandler.ordersInfo = function() {
    console.log(jwt_token);

    cleanData = {};
    cleanData['resource'] = 'customer';
    cleanData['type'] = 'CustomerAccount';
    cleanData['jwt'] = jwt_token;

    $.ajax({
        type: "POST",
        url: 'https://dh0y47otf3.execute-api.us-west-2.amazonaws.com/prod/customer/order',
        crossDomain: true,
        contentType: 'application/json',
        data: JSON.stringify(cleanData),
        dataType: 'json',
        success: function(service_data){
           if (service_data['status']=='success'){
               orders_data = service_data['orders']
               insertHTML = "";
               // "<div id=\"carouselControls\" class=\"carousel slide\" data-ride=\"carousel\"><div class=\"carousel-inner\" role=\"listbox\">";
 
               var jsLength=0;
               for(var order in orders_data){
                    // alert(order);
                    // insertHTML = insertHTML + "<p>"+ order['id'] + "</p>";
                    // insertHTML = insertHTML + "<p>"+ order['item_name'] + "</p>";
                    // insertHTML = insertHTML + "<p>"+ order['price'] + "</p>";
                    // insertHTML = insertHTML + "<p>"+ order['address'] + "</p>";
                    // insertHTML = insertHTML + "<p>"+ order['payment_method'] + "</p>";
                    // insertHTML = insertHTML + "<p>"+ order['time'] + "</p>";
                    jsLength++;
               }       
               for(var i=0;i<jsLength;i++){
                    // insertHTML = insertHTML + "<p>"+ orders_data[i]['id'] + "</p>";
                    // insertHTML = insertHTML + "<p>"+ orders_data[i]['item_name'] + "</p>";
                    // insertHTML = insertHTML + "<p>"+ orders_data[i]['price'] + "</p>";
                    // insertHTML = insertHTML + "<p>"+ orders_data[i]['address'] + "</p>";
                    // insertHTML = insertHTML + "<p>"+ orders_data[i]['payment_method'] + "</p>";
                    // insertHTML = insertHTML + "<p>"+ orders_data[i]['time'] + "</p>";
                    // insertHTML = insertHTML + "<div class=\"carousel-item active\"><img class=\"d-block img-fluid\" src=\"images/" + orders_data[i]['item_name'] + ".png></div>";
                    // if (i==0) itemActive = " active"; else itemActive = "";
                    // insertHTML = insertHTML + "<div class=\"carousel-item" + itemActive + "\"><img class=\"d-block img-fluid\" src=\"images/Practical%20DevOps.png\"></div>";
                    insertHTML = insertHTML + "<div class=\"card text-center\">";
                    insertHTML = insertHTML + "<div class=\"card-header\">";
                    insertHTML = insertHTML + "Order ID: #" + orders_data[i]['id'];
                    insertHTML = insertHTML + "</div>";
                    insertHTML = insertHTML + "<div class=\"card-block\">";
                    insertHTML = insertHTML + "<h4 class=\"card-title\">";
                    insertHTML = insertHTML + orders_data[i]['item_name'] + "</h4>";
                    insertHTML = insertHTML + "<p class=\"card-text\">";

                    insertHTML = insertHTML + "<table class=\"table table-hover\">";
                    insertHTML = insertHTML + "<thead><tr>" + "<th>Details</th>" + "</tr></thead>";
                    insertHTML = insertHTML + "<tbody>";
                    insertHTML = insertHTML + "<tr>" + "<td>Price</td><td>" + orders_data[i]['price']+ "</td></tr>";
                    insertHTML = insertHTML + "<tr>" + "<td>Address</td><td>" + orders_data[i]['address']+ "</td></tr>";
                    insertHTML = insertHTML + "<tr>" + "<td>Payment Method</td><td>" + orders_data[i]['payment_method']+ "</td></tr>";
                    insertHTML = insertHTML + "<tr>" + "<td>Time</td><td>" + orders_data[i]['time']+ "</td></tr>";
                    insertHTML = insertHTML + "</tbody>";
                    insertHTML = insertHTML + "</table>";

                    insertHTML = insertHTML + "</p>";
                    // insertHTML = insertHTML + "<a href=\"#\" class=\"btn btn-primary\">Go somewhere</a>";
                    insertHTML = insertHTML + "</div>" + "<div class=\"card-footer text-muted\">";
                    // insertHTML = insertHTML + orders_data[i]['time'] + "</div></div>";
                    insertHTML = insertHTML + "</div></div>";
               }
               // alert(insertHTML);
               $("#ordersContent").html(insertHTML);
           }
           else{
               insertHTML = "";
               $("#ordersContent").html(insertHTML);
           }
        },
        error: function (e) {
           alert("Unable to retrieve your data.");
        }
    });
}

accountDisplayHandler.cartInfo = function() {
    total_price = 0;

    cleanData = {};
    cleanData['resource'] = "shoppingcart";
    cleanData['type'] = "GetCart";
    cleanData['jwt'] = jwt_token;
    $.ajax({
        type: "POST",
        url: 'https://dh0y47otf3.execute-api.us-west-2.amazonaws.com/prod/customer/shoppingcart',
        crossDomain: true,
        contentType: 'application/json',
        data: JSON.stringify(cleanData),
        dataType: 'json',
        success: function(service_data) {
            if (service_data['status']=='success'){
               items_data = service_data['items'];
               innerHTML = "";
               innerHTML = innerHTML + "<table class=\"table\">";
               innerHTML = innerHTML + "<thead><tr>";
               innerHTML = innerHTML + "<th>#</th>";
               innerHTML = innerHTML + "<th>Name</th>";
               innerHTML = innerHTML + "<th>Description</th>";
               innerHTML = innerHTML + "<th>Price</th>";
               innerHTML = innerHTML + "<th>Quantity</th>";
               innerHTML = innerHTML + "</tr></thead>";
               var jsLength=0;
               cart_items = [];
               for(var item in items_data){
                    jsLength++;
               }
               innerHTML = innerHTML + "<tbody>";
               for(var i=0;i<jsLength;i++){
                    cart_items.push(items_data[i]['id']);
                    total_price = total_price + parseFloat(items_data[i]['price']);

                    innerHTML = innerHTML + "<tr>";
                    innerHTML = innerHTML + "<th scope=\"row\">" + (i+1) + "</th>";
                    innerHTML = innerHTML + "<td>" + items_data[i]['name'] + "</td>";
                    innerHTML = innerHTML + "<td>" + items_data[i]['description'] + "</td>";
                    innerHTML = innerHTML + "<td>" + items_data[i]['price'] + "</td>";

                    innerHTML = innerHTML + "<td>" + items_data[i]['quantity'] + "</td>";
                    innerHTML = innerHTML + "</tr>";
               }
               innerHTML = innerHTML + "</tbody></table>";
               $("#cartContent").html(innerHTML);
            }
            else{
               // alert("No accessibility.");
               innerHTML = "";
               $("#cartContent").html(innerHTML);
            }

            $( "#paymentButton" ).click(function(e) {
              handler.open({
                  name: 'Cart',
                  description: 'Payment for books',
                  amount: total_price * 100
              });
              e.preventDefault();
            });
        },
        error: function (e) {
           alert("Unable to retrieve your data.");
        }
    });
}

accountDisplayHandler.emptyCart = function () {
    console.log(cart_items);
    var cartLength=0;
    for(var item in cart_items){
        cartLength++;
    }

    cleanData = {};
    cleanData['resource'] = "shoppingcart";
    cleanData['type'] = "DeleteCart";
    cleanData['jwt'] = jwt_token;
    for (var i=0;i<cartLength;i++){
        cleanData['item_id'] = cart_items[i];
        $.ajax({
            type: "POST",
            url: 'https://dh0y47otf3.execute-api.us-west-2.amazonaws.com/prod/customer/shoppingcart',
            crossDomain: true,
            contentType: 'application/json',
            data: JSON.stringify(cleanData),
            dataType: 'json',
            success: function(service_data) {
                if (service_data['status']=='success'){
                    //
                }
                else{
                    alert("No accessibility.");
                }
            },
            error: function (e) {
                alert("Unable to retrieve your data.");
            }
        });
    }
    cart_items = [];
    innerHTML = "";
    $("#cartContent").html(innerHTML);
}

function login(formData) {
    cleanData = {};
    cleanData['resource'] = "customer";
    cleanData['type'] = "LogIn";
    cleanData['customer'] = {
        'id':       formData['email'],
        'password': formData['password']
    }
    // console.log(cleanData);
    
    $.ajax({
        type: "POST",
        url: 'https://dh0y47otf3.execute-api.us-west-2.amazonaws.com/prod/customer/login',
        crossDomain: true,
        contentType: 'application/json',
        // header:{
        //     'Access-Control-Allow-Origin': "*"
        // },
        data: JSON.stringify(cleanData),
        dataType: 'json',
        success: function(service_data){
           if (service_data['status']=='success'){
               accountDisplayHandler.logIn(formData.email);
               $('#loginModal').modal('hide')
               //set jwt_token
               setCookie("jwt_token", service_data['jwt']);
           }
           else{
               alert("Invalid email or password.");
               accountDisplayHandler.logOut();
           }
        },
        error: function (e) {
           alert("error");
           accountDisplayHandler.logOut();
        }
    });
}

function signUp(formData) { //new acccount
    cleanData = {};
    cleanData['resource'] = "customer";
    cleanData['type'] = "SignUp";
    cleanData['customer'] = {
        'id':           formData['email'],
        'first_name':   formData['first_name'],
        'last_name':    formData['last_name'],
        'password':     formData['password'],
        'date_of_birth': formData['date_of_birth']
    }
    
    $.ajax({
         type: "POST",
         url: 'https://dh0y47otf3.execute-api.us-west-2.amazonaws.com/prod/customer/signup',
         crossDomain: true,
         contentType: 'application/json',
         
         data: JSON.stringify(cleanData),
         dataType: 'json',
         success: function(data) {
             //TODO
             // accountDisplayHandler.logIn(formData.email);
             $('#signUpModal').modal('hide')
             alert("Please confirm your email.");
         },
         failure: function (data) {
             alert(data.errorMessage)
         }
     });
}

function addCart() {
    // alert("add item!");
    alert("Add to Cart");
    if (jwt_token==""){
        alert("Please sign in first.");
        return;
    }
    cleanData = {};
    cleanData['resource'] = "shoppingcart";
    cleanData['type'] = "AddCart";
    cleanData['item_id'] = items_id[parseInt(event.target.value)];
    cleanData['jwt'] = jwt_token;
    console.log(cleanData);
    $.ajax({
        type: "POST",
        url: 'https://dh0y47otf3.execute-api.us-west-2.amazonaws.com/prod/customer/shoppingcart',
        crossDomain: true,
        contentType: 'application/json',
        data: JSON.stringify(cleanData),
        dataType: 'json',
        success: function(service_data) {
           // alert("Success!");
        },
        error: function (e) {
           alert("Unable to add items.");
        }
    });
}

function ResponseHandler(e, item_id) {
    e.preventDefault();
    console.log("response function");
    $.ajax({
      url: $('#item'+item_id).attr('action'),
      type: "POST",
      data : $('#item'+item_id).serialize(),
      success: function(response){
        console.log('form submitted.');
        console.log(response);
      }
    });

    // var ccNum = $('.data-key').val(),
    //     cvcNum = $('.card-cvc').val(),
    //     expMonth = $('.card-expiry-month').val(),
    //     expYear = $('.card-expiry-year').val();

    // cleanData = {}
    // cleanData['operation'] = "create";
    // cleanData['item_id'] = item_id;
    // // cleanData['item_name'] = data-name;
    // // cleanData['price'] = data-amount;
    // if (item_id=="1"){
    //     cleanData['item_name'] = "Responsive Web Design with HTML5 and CSS3";
    //     cleanData['item_amount'] = "1999";
    // }
    // cleanData['card_number'] = ccNum;
    // cleanData['cvc'] = cvcNum;
    // cleanData['exp_month'] = expMonth;
    // cleanData['exp_year'] = expYear;
    // cleanData['jwt'] = jwt_token;
    // alert(cleanData);
    // $.ajax({
    //     type: "POST",
    //     url: 'https://aztwsjvkm5.execute-api.us-west-2.amazonaws.com/prod/customer/login',
    //     crossDomain: true,
    //     contentType: 'application/json',
    //     data: JSON.stringify(cleanData),
    //     dataType: 'json',
    // });
}

$(document).ready(function() {
    render_items();

    accountDisplayHandler.logOut();

    $('#loginForm').submit(function (e) {
        e.preventDefault();
        login($(this).serializeArray().reduce(
            function(accumulater, curr) {
                accumulater[curr.name] = curr.value;
                return accumulater;
            }, {}));
    });
    $('#signUpForm').submit(function (e) {
        e.preventDefault();
        var formData = $(this).serializeArray().reduce(
            function(accumulater, curr) {
                accumulater[curr.name] = curr.value;
                return accumulater;
            }
            , {});
        if(formData["password"] !== formData["passwordCheck"]) {
            alert("Both password entries must match.");
            return;
        } else if (formData["password"].length < 4) {
            alert("Passwords must be at least 5 character in length.");
            return;
        }
        signUp(formData);
    });
    
    $( "#usernameNavElement" ).click(function() {
        accountDisplayHandler.userInfo();
    });
    $( "#ordersInfoNavElement" ).click(function() {
        accountDisplayHandler.ordersInfo();
    });
    $( "#cartNavElement" ).click(function() {
        accountDisplayHandler.cartInfo();
    });
    $( "#logoutNavElement" ).click(function() {
        accountDisplayHandler.logOut();
    });
    $( "#emptyCart" ).click(function() {
        accountDisplayHandler.emptyCart();
    });
  

    $( "#datetimepicker" ).datetimepicker({
        pickTime: false
    });
});

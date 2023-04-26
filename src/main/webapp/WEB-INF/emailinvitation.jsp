<!DOCTYPE html>
<html lang="en">

<head>
    <style type="text/css">
        @import url('https://fonts.googleapis.com/css2?family=Lato:wght@400;900&display=swap');

        * {
            margin: 0;
            border: 0;
            padding: 0;
        }

        body {
            font-family: 'Lato', sans-serif;
            background-color: #d8dbdb;
            font-size: 18px;
            max-width: 700px;
            margin: 0 auto;
            padding: 2%;
            color: #252527;
        }

        .wrapper {
            background: #f6faff;
            box-shadow: 0 0 10px #666;
        }

        img {
            max-width: 100%;
        }

        .social {
            width: 98%;
            text-align: center;
            list-style-type: none;
            padding: 1%;
            background-image: linear-gradient(to right, #ff911f, #05A08C, #005247);
        }

        .social li {
            display: inline;
        }

        .social img {
            margin: auto;
            max-width: 45px;
        }

        .logotxt {
            width: 100%;
            margin: 1% 0;
            text-align: center;
            color: #005247;
        }

        .logo {
            margin: 20px auto 0 auto;
            max-width: 200px;
        }

        h2 {
            letter-spacing: 1%;
            padding-bottom: 2%;
            color: #05A08C;
        }

        p {
            text-align: justify;
            line-height: 20px;
        }

        .button-holder {
            text-align: center;
            margin: 4% 2%;
        }

        .btn {
            text-align: center;
            background: #252527;
            color: #fff;
            text-decoration: none;
            font-weight: 600;
            padding: 10px 14px;
            border-radius: 8px;
            letter-spacing: 2px;
        }

        .line {
            clear: both;
            height: 1px;
            background-image: linear-gradient(to right, #ff911f, #05A08C, #005247);
            margin: 6% auto;
            width: 98%;
        }

        .two-col {

            margin: 4% 0;
            width: 96%;
            padding: 2%;
        }

        .contactinfo {
            text-align: center;
            padding-bottom: 6%;
        }

        @media(max-width: 600px) {
            body {
                padding: 1%;
            }
            .two-col{
                min-width: 96%;
                margin: 0!important;
            }

        }
    </style>

</head>

<body>
    <div class="wrapper">
        <ul class="social">
            <li><a href="#" target="_blank"><img src="https://i.ibb.co/K6mqZXr/facebook.png" alt=""></a>
            </li>
            <li><a href="#" target="_blank"><img src="https://i.ibb.co/zJg1dpj/twitter.png" alt=""></a>
            </li>
            <li><a href="#" target="_blank"><img src="https://i.ibb.co/y6vwKSt/linkedin.png" alt=""></a>
            </li>
        </ul>
        <div class="logo">
            <img src="https://i.ibb.co/KqG49qm/grzegorz.png" alt="">
        </div>
        <div class="two-col">
            <h2>Organization Invitation</h2>
            <img src="https://i.ibb.co/5xxMqff/post-1.png" alt="">
            <p>On behalf of GrzegorzEX, we are pleased to inform you that you have been invited to an "organization".</p>
            <div class="button-holder">
                <a href="${link}" class="btn" target="">Join now!</a>
            </div>
        </div>
        <div class="line"></div>
        <div class="logotxt">
            <h2>GrzegorzEX</h2>
        </div>
        <p class="contactinfo">
            www.grzegorzex.com <br> 1 (XXX) XXX-XXXX <br> 2 Road Name - City - State <br> hoar.se.2022@gmail.com
        </p>
    </div>
</body>

</html>
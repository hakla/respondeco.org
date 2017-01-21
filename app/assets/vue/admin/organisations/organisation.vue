<template>
<div class="">
  <div class="page-title">
    <div class="title_left">
      <h3>Form Elements</h3>
    </div>

    <div class="title_right">
      <div class="col-md-5 col-sm-5 col-xs-12 form-group pull-right top_search">
        <div class="input-group">
          <input type="text" class="form-control" placeholder="Search for...">
          <span class="input-group-btn">
                        <button class="btn btn-default" type="button">Go!</button>
                      </span>
        </div>
      </div>
    </div>
  </div>
  <div class="clearfix"></div>
  <div class="row">
    <div class="col-md-12 col-sm-12 col-xs-12">
      <div class="x_panel">
        <div class="x_title">
          <h2>Form Design <small>different form elements</small></h2>
          <ul class="nav navbar-right panel_toolbox">
            <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
            </li>
            <li class="dropdown">
              <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false"><i class="fa fa-wrench"></i></a>
              <ul class="dropdown-menu" role="menu">
                <li><a href="#">Settings 1</a>
                </li>
                <li><a href="#">Settings 2</a>
                </li>
              </ul>
            </li>
            <li><a class="close-link"><i class="fa fa-close"></i></a>
            </li>
          </ul>
          <div class="clearfix"></div>
        </div>
        <div class="x_content">
          <br>
          <form id="demo-form2" data-parsley-validate="" class="form-horizontal form-label-left" novalidate="">

            <div class="form-group">
              <label class="control-label col-md-3 col-sm-3 col-xs-12" for="first-name">First Name <span class="required">*</span>
                          </label>
              <div class="col-md-6 col-sm-6 col-xs-12">
                <input type="text" id="first-name" required="required" class="form-control col-md-7 col-xs-12" style="background-image: url(&quot;data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAABHklEQVQ4EaVTO26DQBD1ohQWaS2lg9JybZ+AK7hNwx2oIoVf4UPQ0Lj1FdKktevIpel8AKNUkDcWMxpgSaIEaTVv3sx7uztiTdu2s/98DywOw3Dued4Who/M2aIx5lZV1aEsy0+qiwHELyi+Ytl0PQ69SxAxkWIA4RMRTdNsKE59juMcuZd6xIAFeZ6fGCdJ8kY4y7KAuTRNGd7jyEBXsdOPE3a0QGPsniOnnYMO67LgSQN9T41F2QGrQRRFCwyzoIF2qyBuKKbcOgPXdVeY9rMWgNsjf9ccYesJhk3f5dYT1HX9gR0LLQR30TnjkUEcx2uIuS4RnI+aj6sJR0AM8AaumPaM/rRehyWhXqbFAA9kh3/8/NvHxAYGAsZ/il8IalkCLBfNVAAAAABJRU5ErkJggg==&quot;); background-repeat: no-repeat; background-attachment: scroll; background-size: 16px 18px; background-position: 98% 50%;">
              </div>
            </div>
            <div class="form-group">
              <label class="control-label col-md-3 col-sm-3 col-xs-12" for="last-name">Last Name <span class="required">*</span>
                          </label>
              <div class="col-md-6 col-sm-6 col-xs-12">
                <input type="text" id="last-name" name="last-name" required="required" class="form-control col-md-7 col-xs-12">
              </div>
            </div>
            <div class="form-group">
              <label for="middle-name" class="control-label col-md-3 col-sm-3 col-xs-12">Middle Name / Initial</label>
              <div class="col-md-6 col-sm-6 col-xs-12">
                <input id="middle-name" class="form-control col-md-7 col-xs-12" type="text" name="middle-name">
              </div>
            </div>
            <div class="form-group">
              <label class="control-label col-md-3 col-sm-3 col-xs-12">Gender</label>
              <div class="col-md-6 col-sm-6 col-xs-12">
                <div id="gender" class="btn-group" data-toggle="buttons">
                  <label class="btn btn-default" data-toggle-class="btn-primary" data-toggle-passive-class="btn-default">
                                <input type="radio" name="gender" value="male" data-parsley-multiple="gender"> &nbsp; Male &nbsp;
                              </label>
                  <label class="btn btn-primary" data-toggle-class="btn-primary" data-toggle-passive-class="btn-default">
                                <input type="radio" name="gender" value="female" data-parsley-multiple="gender"> Female
                              </label>
                </div>
              </div>
            </div>
            <div class="form-group">
              <label class="control-label col-md-3 col-sm-3 col-xs-12">Date Of Birth <span class="required">*</span>
                          </label>
              <div class="col-md-6 col-sm-6 col-xs-12">
                <input id="birthday" class="date-picker form-control col-md-7 col-xs-12" required="required" type="text">
              </div>
            </div>
            <div class="ln_solid"></div>
            <div class="form-group">
              <div class="col-md-6 col-sm-6 col-xs-12 col-md-offset-3">
                <button type="submit" class="btn btn-primary">Cancel</button>
                <button type="submit" class="btn btn-success">Submit</button>
              </div>
            </div>

          </form>
        </div>
      </div>
    </div>
  </div>

  <div class="row">
    <div class="col-md-6 col-xs-12">
      <div class="x_panel">
        <div class="x_title">
          <h2>Form Design <small>different form elements</small></h2>
          <ul class="nav navbar-right panel_toolbox">
            <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
            </li>
            <li class="dropdown">
              <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false"><i class="fa fa-wrench"></i></a>
              <ul class="dropdown-menu" role="menu">
                <li><a href="#">Settings 1</a>
                </li>
                <li><a href="#">Settings 2</a>
                </li>
              </ul>
            </li>
            <li><a class="close-link"><i class="fa fa-close"></i></a>
            </li>
          </ul>
          <div class="clearfix"></div>
        </div>
        <div class="x_content">
          <br>
          <form class="form-horizontal form-label-left input_mask">

            <div class="col-md-6 col-sm-6 col-xs-12 form-group has-feedback">
              <input type="text" class="form-control has-feedback-left" id="inputSuccess2" placeholder="First Name" style="background-image: url(&quot;data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAABHklEQVQ4EaVTO26DQBD1ohQWaS2lg9JybZ+AK7hNwx2oIoVf4UPQ0Lj1FdKktevIpel8AKNUkDcWMxpgSaIEaTVv3sx7uztiTdu2s/98DywOw3Dued4Who/M2aIx5lZV1aEsy0+qiwHELyi+Ytl0PQ69SxAxkWIA4RMRTdNsKE59juMcuZd6xIAFeZ6fGCdJ8kY4y7KAuTRNGd7jyEBXsdOPE3a0QGPsniOnnYMO67LgSQN9T41F2QGrQRRFCwyzoIF2qyBuKKbcOgPXdVeY9rMWgNsjf9ccYesJhk3f5dYT1HX9gR0LLQR30TnjkUEcx2uIuS4RnI+aj6sJR0AM8AaumPaM/rRehyWhXqbFAA9kh3/8/NvHxAYGAsZ/il8IalkCLBfNVAAAAABJRU5ErkJggg==&quot;); background-repeat: no-repeat; background-attachment: scroll; background-size: 16px 18px; background-position: 98% 50%;">
              <span class="fa fa-user form-control-feedback left" aria-hidden="true"></span>
            </div>

            <div class="col-md-6 col-sm-6 col-xs-12 form-group has-feedback">
              <input type="text" class="form-control" id="inputSuccess3" placeholder="Last Name">
              <span class="fa fa-user form-control-feedback right" aria-hidden="true"></span>
            </div>

            <div class="col-md-6 col-sm-6 col-xs-12 form-group has-feedback">
              <input type="text" class="form-control has-feedback-left" id="inputSuccess4" placeholder="Email">
              <span class="fa fa-envelope form-control-feedback left" aria-hidden="true"></span>
            </div>

            <div class="col-md-6 col-sm-6 col-xs-12 form-group has-feedback">
              <input type="text" class="form-control" id="inputSuccess5" placeholder="Phone">
              <span class="fa fa-phone form-control-feedback right" aria-hidden="true"></span>
            </div>

            <div class="form-group">
              <label class="control-label col-md-3 col-sm-3 col-xs-12">Default Input</label>
              <div class="col-md-9 col-sm-9 col-xs-12">
                <input type="text" class="form-control" placeholder="Default Input">
              </div>
            </div>
            <div class="form-group">
              <label class="control-label col-md-3 col-sm-3 col-xs-12">Disabled Input </label>
              <div class="col-md-9 col-sm-9 col-xs-12">
                <input type="text" class="form-control" disabled="disabled" placeholder="Disabled Input">
              </div>
            </div>
            <div class="form-group">
              <label class="control-label col-md-3 col-sm-3 col-xs-12">Read-Only Input</label>
              <div class="col-md-9 col-sm-9 col-xs-12">
                <input type="text" class="form-control" readonly="readonly" placeholder="Read-Only Input">
              </div>
            </div>
            <div class="form-group">
              <label class="control-label col-md-3 col-sm-3 col-xs-12">Date Of Birth <span class="required">*</span>
                          </label>
              <div class="col-md-9 col-sm-9 col-xs-12">
                <input class="date-picker form-control col-md-7 col-xs-12" required="required" type="text">
              </div>
            </div>
            <div class="ln_solid"></div>
            <div class="form-group">
              <div class="col-md-9 col-sm-9 col-xs-12 col-md-offset-3">
                <button type="submit" class="btn btn-primary">Cancel</button>
                <button type="submit" class="btn btn-success">Submit</button>
              </div>
            </div>

          </form>
        </div>
      </div>

      <div class="x_panel">
        <div class="x_title">
          <h2>Star Rating</h2>
          <ul class="nav navbar-right panel_toolbox">
            <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
            </li>
            <li class="dropdown">
              <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false"><i class="fa fa-wrench"></i></a>
              <ul class="dropdown-menu" role="menu">
                <li><a href="#">Settings 1</a>
                </li>
                <li><a href="#">Settings 2</a>
                </li>
              </ul>
            </li>
            <li><a class="close-link"><i class="fa fa-close"></i></a>
            </li>
          </ul>
          <div class="clearfix"></div>
        </div>
        <div class="x_content">
          <h4>Star Ratings<small> Hover and click on a star</small></h4>
          <div>
            <div class="starrr stars">
              <a href="#" class="fa fa-star-o"></a>
              <a href="#" class="fa fa-star-o"></a>
              <a href="#" class="fa fa-star-o"></a>
              <a href="#" class="fa fa-star-o"></a>
              <a href="#" class="fa fa-star-o"></a>
            </div>
            You gave a rating of <span class="stars-count">0</span> star(s)
          </div>

          <p>Also you can give a default rating by adding attribute data-rating</p>
          <div class="starrr stars-existing" data-rating="4">
            <a href="#" class="fa fa-star"></a>
            <a href="#" class="fa fa-star"></a>
            <a href="#" class="fa fa-star"></a>
            <a href="#" class="fa fa-star"></a>
            <a href="#" class="fa fa-star-o"></a>
          </div>
          You gave a rating of <span class="stars-count-existing">4</span> star(s)
        </div>
      </div>

      <div class="x_panel">
        <div class="x_title">
          <h2>Registration Form <small>Click to validate</small></h2>
          <ul class="nav navbar-right panel_toolbox">
            <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
            </li>
            <li class="dropdown">
              <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false"><i class="fa fa-wrench"></i></a>
              <ul class="dropdown-menu" role="menu">
                <li><a href="#">Settings 1</a>
                </li>
                <li><a href="#">Settings 2</a>
                </li>
              </ul>
            </li>
            <li><a class="close-link"><i class="fa fa-close"></i></a>
            </li>
          </ul>
          <div class="clearfix"></div>
        </div>
        <div class="x_content">

          <!-- start form for validation -->
          <form id="demo-form" data-parsley-validate="" novalidate="">
            <label for="fullname">Full Name * :</label>
            <input type="text" id="fullname" class="form-control" name="fullname" required="">

            <label for="email">Email * :</label>
            <input type="email" id="email" class="form-control" name="email" data-parsley-trigger="change" required="">

            <label>Gender *:</label>
            <p>
              M:
              <div class="iradio_flat-green checked" style="position: relative;"><input type="radio" class="flat" name="gender" id="genderM" value="M" checked="" required="" data-parsley-multiple="gender" style="position: absolute; opacity: 0;"><ins class="iCheck-helper" style="position: absolute; top: 0%; left: 0%; display: block; width: 100%; height: 100%; margin: 0px; padding: 0px; background: rgb(255, 255, 255); border: 0px; opacity: 0;"></ins></div>
              F:
              <div class="iradio_flat-green" style="position: relative;"><input type="radio" class="flat" name="gender" id="genderF" value="F" data-parsley-multiple="gender" style="position: absolute; opacity: 0;"><ins class="iCheck-helper" style="position: absolute; top: 0%; left: 0%; display: block; width: 100%; height: 100%; margin: 0px; padding: 0px; background: rgb(255, 255, 255); border: 0px; opacity: 0;"></ins></div>
            </p>

            <label>Hobbies (2 minimum):</label>
            <p style="padding: 5px;">
              <div class="icheckbox_flat-green" style="position: relative;"><input type="checkbox" name="hobbies[]" id="hobby1" value="ski" data-parsley-mincheck="2" required="" class="flat" data-parsley-multiple="hobbies" style="position: absolute; opacity: 0;"><ins class="iCheck-helper" style="position: absolute; top: 0%; left: 0%; display: block; width: 100%; height: 100%; margin: 0px; padding: 0px; background: rgb(255, 255, 255); border: 0px; opacity: 0;"></ins></div>
              Skiing
              <br>

              <div class="icheckbox_flat-green" style="position: relative;"><input type="checkbox" name="hobbies[]" id="hobby2" value="run" class="flat" data-parsley-multiple="hobbies" style="position: absolute; opacity: 0;"><ins class="iCheck-helper" style="position: absolute; top: 0%; left: 0%; display: block; width: 100%; height: 100%; margin: 0px; padding: 0px; background: rgb(255, 255, 255); border: 0px; opacity: 0;"></ins></div>
              Running
              <br>

              <div class="icheckbox_flat-green" style="position: relative;"><input type="checkbox" name="hobbies[]" id="hobby3" value="eat" class="flat" data-parsley-multiple="hobbies" style="position: absolute; opacity: 0;"><ins class="iCheck-helper" style="position: absolute; top: 0%; left: 0%; display: block; width: 100%; height: 100%; margin: 0px; padding: 0px; background: rgb(255, 255, 255); border: 0px; opacity: 0;"></ins></div>
              Eating
              <br>

              <div class="icheckbox_flat-green" style="position: relative;"><input type="checkbox" name="hobbies[]" id="hobby4" value="sleep" class="flat" data-parsley-multiple="hobbies" style="position: absolute; opacity: 0;"><ins class="iCheck-helper" style="position: absolute; top: 0%; left: 0%; display: block; width: 100%; height: 100%; margin: 0px; padding: 0px; background: rgb(255, 255, 255); border: 0px; opacity: 0;"></ins></div>
              Sleeping
              <br>
            </p>
            <p>

              <label for="heard">Heard us by *:</label>
              <select id="heard" class="form-control" required="">
                              <option value="">Choose..</option>
                              <option value="press">Press</option>
                              <option value="net">Internet</option>
                              <option value="mouth">Word of mouth</option>
                            </select>

              <label for="message">Message (20 chars min, 100 max) :</label>
              <textarea id="message" required="required" class="form-control" name="message" data-parsley-trigger="keyup" data-parsley-minlength="20" data-parsley-maxlength="100" data-parsley-minlength-message="Come on! You need to enter at least a 20 caracters long comment.."
                  data-parsley-validation-threshold="10"></textarea>

              <br>
              <span class="btn btn-primary">Validate form</span>

            </p>
          </form>
          <!-- end form for validations -->

        </div>
      </div>


    </div>

    <div class="col-md-6 col-xs-12">
      <div class="x_panel">
        <div class="x_title">
          <h2>Form Basic Elements <small>different form elements</small></h2>
          <ul class="nav navbar-right panel_toolbox">
            <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
            </li>
            <li class="dropdown">
              <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false"><i class="fa fa-wrench"></i></a>
              <ul class="dropdown-menu" role="menu">
                <li><a href="#">Settings 1</a>
                </li>
                <li><a href="#">Settings 2</a>
                </li>
              </ul>
            </li>
            <li><a class="close-link"><i class="fa fa-close"></i></a>
            </li>
          </ul>
          <div class="clearfix"></div>
        </div>
        <div class="x_content">
          <br>
          <form class="form-horizontal form-label-left">

            <div class="form-group">
              <label class="control-label col-md-3 col-sm-3 col-xs-12">Default Input</label>
              <div class="col-md-9 col-sm-9 col-xs-12">
                <input type="text" class="form-control" placeholder="Default Input" style="background-image: url(&quot;data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAABHklEQVQ4EaVTO26DQBD1ohQWaS2lg9JybZ+AK7hNwx2oIoVf4UPQ0Lj1FdKktevIpel8AKNUkDcWMxpgSaIEaTVv3sx7uztiTdu2s/98DywOw3Dued4Who/M2aIx5lZV1aEsy0+qiwHELyi+Ytl0PQ69SxAxkWIA4RMRTdNsKE59juMcuZd6xIAFeZ6fGCdJ8kY4y7KAuTRNGd7jyEBXsdOPE3a0QGPsniOnnYMO67LgSQN9T41F2QGrQRRFCwyzoIF2qyBuKKbcOgPXdVeY9rMWgNsjf9ccYesJhk3f5dYT1HX9gR0LLQR30TnjkUEcx2uIuS4RnI+aj6sJR0AM8AaumPaM/rRehyWhXqbFAA9kh3/8/NvHxAYGAsZ/il8IalkCLBfNVAAAAABJRU5ErkJggg==&quot;); background-repeat: no-repeat; background-attachment: scroll; background-size: 16px 18px; background-position: 98% 50%;">
              </div>
            </div>
            <div class="form-group">
              <label class="control-label col-md-3 col-sm-3 col-xs-12">Disabled Input </label>
              <div class="col-md-9 col-sm-9 col-xs-12">
                <input type="text" class="form-control" disabled="disabled" placeholder="Disabled Input">
              </div>
            </div>
            <div class="form-group">
              <label class="control-label col-md-3 col-sm-3 col-xs-12">Read-Only Input</label>
              <div class="col-md-9 col-sm-9 col-xs-12">
                <input type="text" class="form-control" readonly="readonly" placeholder="Read-Only Input">
              </div>
            </div>
            <div class="form-group">
              <label class="control-label col-md-3 col-sm-3 col-xs-12">Date Of Birth <span class="required">*</span>
                          </label>
              <div class="col-md-9 col-sm-9 col-xs-12">
                <textarea class="form-control" rows="3" placeholder="rows=&quot;3&quot;"></textarea>
              </div>
            </div>
            <div class="form-group">
              <label class="control-label col-md-3 col-sm-3 col-xs-12">Password</label>
              <div class="col-md-9 col-sm-9 col-xs-12">
                <input type="password" class="form-control" value="passwordonetwo" style="background-image: url(&quot;data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAACIUlEQVQ4EX2TOYhTURSG87IMihDsjGghBhFBmHFDHLWwSqcikk4RRKJgk0KL7C8bMpWpZtIqNkEUl1ZCgs0wOo0SxiLMDApWlgOPrH7/5b2QkYwX7jvn/uc//zl3edZ4PPbNGvF4fC4ajR5VrNvt/mo0Gr1ZPOtfgWw2e9Lv9+chX7cs64CS4Oxg3o9GI7tUKv0Q5o1dAiTfCgQCLwnOkfQOu+oSLyJ2A783HA7vIPLGxX0TgVwud4HKn0nc7Pf7N6vV6oZHkkX8FPG3uMfgXC0Wi2vCg/poUKGGcagQI3k7k8mcp5slcGswGDwpl8tfwGJg3xB6Dvey8vz6oH4C3iXcFYjbwiDeo1KafafkC3NjK7iL5ESFGQEUF7Sg+ifZdDp9GnMF/KGmfBdT2HCwZ7TwtrBPC7rQaav6Iv48rqZwg+F+p8hOMBj0IbxfMdMBrW5pAVGV/ztINByENkU0t5BIJEKRSOQ3Aj+Z57iFs1R5NK3EQS6HQqF1zmQdzpFWq3W42WwOTAf1er1PF2USFlC+qxMvFAr3HcexWX+QX6lUvsKpkTyPSEXJkw6MQ4S38Ljdbi8rmM/nY+CvgNcQqdH6U/xrYK9t244jZv6ByUOSiDdIfgBZ12U6dHEHu9TpdIr8F0OP692CtzaW/a6y3y0Wx5kbFHvGuXzkgf0xhKnPzA4UTyaTB8Ph8AvcHi3fnsrZ7Wore02YViqVOrRXXPhfqP8j6MYlawoAAAAASUVORK5CYII=&quot;); background-repeat: no-repeat; background-attachment: scroll; background-size: 16px 18px; background-position: 98% 50%;">
              </div>
            </div>
            <div class="form-group">
              <label class="control-label col-md-3 col-sm-3 col-xs-12">AutoComplete</label>
              <div class="col-md-9 col-sm-9 col-xs-12">
                <input type="text" name="country" id="autocomplete-custom-append" class="form-control col-md-10" style="float: left;" autocomplete="off">
                <div id="autocomplete-container" style="position: relative; float: left; width: 400px; margin: 10px;">
                  <div class="autocomplete-suggestions" style="position: absolute; display: none; max-height: 300px; z-index: 9999;"></div>
                </div>
              </div>
            </div>
            <div class="form-group">
              <label class="control-label col-md-3 col-sm-3 col-xs-12">Select</label>
              <div class="col-md-9 col-sm-9 col-xs-12">
                <select class="form-control">
                              <option>Choose option</option>
                              <option>Option one</option>
                              <option>Option two</option>
                              <option>Option three</option>
                              <option>Option four</option>
                            </select>
              </div>
            </div>
            <div class="form-group">
              <label class="control-label col-md-3 col-sm-3 col-xs-12">Select Custom</label>
              <div class="col-md-9 col-sm-9 col-xs-12">
                <select class="select2_single form-control select2-hidden-accessible" tabindex="-1" aria-hidden="true">
                              <option></option>
                              <option value="AK">Alaska</option>
                              <option value="HI">Hawaii</option>
                              <option value="CA">California</option>
                              <option value="NV">Nevada</option>
                              <option value="OR">Oregon</option>
                              <option value="WA">Washington</option>
                              <option value="AZ">Arizona</option>
                              <option value="CO">Colorado</option>
                              <option value="ID">Idaho</option>
                              <option value="MT">Montana</option>
                              <option value="NE">Nebraska</option>
                              <option value="NM">New Mexico</option>
                              <option value="ND">North Dakota</option>
                              <option value="UT">Utah</option>
                              <option value="WY">Wyoming</option>
                              <option value="AR">Arkansas</option>
                              <option value="IL">Illinois</option>
                              <option value="IA">Iowa</option>
                              <option value="KS">Kansas</option>
                              <option value="KY">Kentucky</option>
                              <option value="LA">Louisiana</option>
                              <option value="MN">Minnesota</option>
                              <option value="MS">Mississippi</option>
                              <option value="MO">Missouri</option>
                              <option value="OK">Oklahoma</option>
                              <option value="SD">South Dakota</option>
                              <option value="TX">Texas</option>
                            </select><span class="select2 select2-container select2-container--default" dir="ltr" style="width: 523px;"><span class="selection"><span class="select2-selection select2-selection--single" role="combobox" aria-haspopup="true" aria-expanded="false" tabindex="-1" aria-labelledby="select2-vri4-container"><span class="select2-selection__rendered" id="select2-vri4-container"><span class="select2-selection__placeholder">Select a state</span></span>
                <span
                    class="select2-selection__arrow" role="presentation"><b role="presentation"></b></span>
                  </span>
                  </span><span class="dropdown-wrapper" aria-hidden="true"></span></span>
              </div>
            </div>
            <div class="form-group">
              <label class="control-label col-md-3 col-sm-3 col-xs-12">Select Grouped</label>
              <div class="col-md-9 col-sm-9 col-xs-12">
                <select class="select2_group form-control select2-hidden-accessible" tabindex="-1" aria-hidden="true">
                              <optgroup label="Alaskan/Hawaiian Time Zone">
                                <option value="AK">Alaska</option>
                                <option value="HI">Hawaii</option>
                              </optgroup>
                              <optgroup label="Pacific Time Zone">
                                <option value="CA">California</option>
                                <option value="NV">Nevada</option>
                                <option value="OR">Oregon</option>
                                <option value="WA">Washington</option>
                              </optgroup>
                              <optgroup label="Mountain Time Zone">
                                <option value="AZ">Arizona</option>
                                <option value="CO">Colorado</option>
                                <option value="ID">Idaho</option>
                                <option value="MT">Montana</option>
                                <option value="NE">Nebraska</option>
                                <option value="NM">New Mexico</option>
                                <option value="ND">North Dakota</option>
                                <option value="UT">Utah</option>
                                <option value="WY">Wyoming</option>
                              </optgroup>
                              <optgroup label="Central Time Zone">
                                <option value="AL">Alabama</option>
                                <option value="AR">Arkansas</option>
                                <option value="IL">Illinois</option>
                                <option value="IA">Iowa</option>
                                <option value="KS">Kansas</option>
                                <option value="KY">Kentucky</option>
                                <option value="LA">Louisiana</option>
                                <option value="MN">Minnesota</option>
                                <option value="MS">Mississippi</option>
                                <option value="MO">Missouri</option>
                                <option value="OK">Oklahoma</option>
                                <option value="SD">South Dakota</option>
                                <option value="TX">Texas</option>
                                <option value="TN">Tennessee</option>
                                <option value="WI">Wisconsin</option>
                              </optgroup>
                              <optgroup label="Eastern Time Zone">
                                <option value="CT">Connecticut</option>
                                <option value="DE">Delaware</option>
                                <option value="FL">Florida</option>
                                <option value="GA">Georgia</option>
                                <option value="IN">Indiana</option>
                                <option value="ME">Maine</option>
                                <option value="MD">Maryland</option>
                                <option value="MA">Massachusetts</option>
                                <option value="MI">Michigan</option>
                                <option value="NH">New Hampshire</option>
                                <option value="NJ">New Jersey</option>
                                <option value="NY">New York</option>
                                <option value="NC">North Carolina</option>
                                <option value="OH">Ohio</option>
                                <option value="PA">Pennsylvania</option>
                                <option value="RI">Rhode Island</option>
                                <option value="SC">South Carolina</option>
                                <option value="VT">Vermont</option>
                                <option value="VA">Virginia</option>
                                <option value="WV">West Virginia</option>
                              </optgroup>
                            </select><span class="select2 select2-container select2-container--default" dir="ltr" style="width: 523px;"><span class="selection"><span class="select2-selection select2-selection--single" role="combobox" aria-haspopup="true" aria-expanded="false" tabindex="0" aria-labelledby="select2-nicj-container"><span class="select2-selection__rendered" id="select2-nicj-container" title="Alaska">Alaska</span>
                <span
                    class="select2-selection__arrow" role="presentation"><b role="presentation"></b></span>
                  </span>
                  </span><span class="dropdown-wrapper" aria-hidden="true"></span></span>
              </div>
            </div>
            <div class="form-group">
              <label class="control-label col-md-3 col-sm-3 col-xs-12">Select Multiple</label>
              <div class="col-md-9 col-sm-9 col-xs-12">
                <select class="select2_multiple form-control select2-hidden-accessible" multiple="" tabindex="-1" aria-hidden="true">
                              <option>Choose option</option>
                              <option>Option one</option>
                              <option>Option two</option>
                              <option>Option three</option>
                              <option>Option four</option>
                              <option>Option five</option>
                              <option>Option six</option>
                            </select><span class="select2 select2-container select2-container--default" dir="ltr" style="width: 523px;"><span class="selection"><span class="select2-selection select2-selection--multiple" role="combobox" aria-haspopup="true" aria-expanded="false" tabindex="-1"><ul class="select2-selection__rendered"><li class="select2-search select2-search--inline"><input class="select2-search__field" type="search" tabindex="0" autocomplete="off" autocorrect="off" autocapitalize="off" spellcheck="false" role="textbox" aria-autocomplete="list" placeholder="With Max Selection limit 4" style="width: 521px;"></li></ul></span></span>
                <span
                    class="dropdown-wrapper" aria-hidden="true"></span>
                  </span>
              </div>
            </div>

            <div class="control-group">
              <label class="control-label col-md-3 col-sm-3 col-xs-12">Input Tags</label>
              <div class="col-md-9 col-sm-9 col-xs-12">
                <input id="tags_1" type="text" class="tags form-control" value="social, adverts, sales" data-tagsinput-init="true" style="display: none;">
                <div id="tags_1_tagsinput" class="tagsinput" style="width: auto; min-height: 100px; height: 100px;"><span class="tag"><span>social&nbsp;&nbsp;</span><a href="#" title="Removing tag">x</a></span><span class="tag"><span>adverts&nbsp;&nbsp;</span><a href="#" title="Removing tag">x</a></span><span class="tag"><span>sales&nbsp;&nbsp;</span>
                  <a
                      href="#" title="Removing tag">x</a>
                    </span>
                    <div id="tags_1_addTag"><input id="tags_1_tag" value="" data-default="add a tag" style="color: rgb(102, 102, 102); width: 72px;"></div>
                    <div class="tags_clear"></div>
                </div>
                <div id="suggestions-container" style="position: relative; float: left; width: 250px; margin: 10px;"></div>
              </div>
            </div>
            <div class="form-group">
              <label class="col-md-3 col-sm-3 col-xs-12 control-label">Checkboxes and radios
                            <br>
                            <small class="text-navy">Normal Bootstrap elements</small>
                          </label>

              <div class="col-md-9 col-sm-9 col-xs-12">
                <div class="checkbox">
                  <label>
                                <input type="checkbox" value=""> Option one. select more than one options
                              </label>
                </div>
                <div class="checkbox">
                  <label>
                                <input type="checkbox" value=""> Option two. select more than one options
                              </label>
                </div>
                <div class="radio">
                  <label>
                                <input type="radio" checked="" value="option1" id="optionsRadios1" name="optionsRadios"> Option one. only select one option
                              </label>
                </div>
                <div class="radio">
                  <label>
                                <input type="radio" value="option2" id="optionsRadios2" name="optionsRadios"> Option two. only select one option
                              </label>
                </div>
              </div>
            </div>

            <div class="form-group">
              <label class="col-md-3 col-sm-3 col-xs-12 control-label">Checkboxes and radios
                            <br>
                            <small class="text-navy">Normal Bootstrap elements</small>
                          </label>

              <div class="col-md-9 col-sm-9 col-xs-12">
                <div class="checkbox">
                  <label>
                                <div class="icheckbox_flat-green checked" style="position: relative;"><input type="checkbox" class="flat" checked="checked" style="position: absolute; opacity: 0;"><ins class="iCheck-helper" style="position: absolute; top: 0%; left: 0%; display: block; width: 100%; height: 100%; margin: 0px; padding: 0px; background: rgb(255, 255, 255); border: 0px; opacity: 0;"></ins></div> Checked
                              </label>
                </div>
                <div class="checkbox">
                  <label>
                                <div class="icheckbox_flat-green" style="position: relative;"><input type="checkbox" class="flat" style="position: absolute; opacity: 0;"><ins class="iCheck-helper" style="position: absolute; top: 0%; left: 0%; display: block; width: 100%; height: 100%; margin: 0px; padding: 0px; background: rgb(255, 255, 255); border: 0px; opacity: 0;"></ins></div> Unchecked
                              </label>
                </div>
                <div class="checkbox">
                  <label>
                                <div class="icheckbox_flat-green disabled" style="position: relative;"><input type="checkbox" class="flat" disabled="disabled" style="position: absolute; opacity: 0;"><ins class="iCheck-helper" style="position: absolute; top: 0%; left: 0%; display: block; width: 100%; height: 100%; margin: 0px; padding: 0px; background: rgb(255, 255, 255); border: 0px; opacity: 0;"></ins></div> Disabled
                              </label>
                </div>
                <div class="checkbox">
                  <label>
                                <div class="icheckbox_flat-green checked disabled" style="position: relative;"><input type="checkbox" class="flat" disabled="disabled" checked="checked" style="position: absolute; opacity: 0;"><ins class="iCheck-helper" style="position: absolute; top: 0%; left: 0%; display: block; width: 100%; height: 100%; margin: 0px; padding: 0px; background: rgb(255, 255, 255); border: 0px; opacity: 0;"></ins></div> Disabled &amp; checked
                              </label>
                </div>
                <div class="radio">
                  <label>
                                <div class="iradio_flat-green checked" style="position: relative;"><input type="radio" class="flat" checked="" name="iCheck" style="position: absolute; opacity: 0;"><ins class="iCheck-helper" style="position: absolute; top: 0%; left: 0%; display: block; width: 100%; height: 100%; margin: 0px; padding: 0px; background: rgb(255, 255, 255); border: 0px; opacity: 0;"></ins></div> Checked
                              </label>
                </div>
                <div class="radio">
                  <label>
                                <div class="iradio_flat-green" style="position: relative;"><input type="radio" class="flat" name="iCheck" style="position: absolute; opacity: 0;"><ins class="iCheck-helper" style="position: absolute; top: 0%; left: 0%; display: block; width: 100%; height: 100%; margin: 0px; padding: 0px; background: rgb(255, 255, 255); border: 0px; opacity: 0;"></ins></div> Unchecked
                              </label>
                </div>
                <div class="radio">
                  <label>
                                <div class="iradio_flat-green disabled" style="position: relative;"><input type="radio" class="flat" name="iCheck" disabled="disabled" style="position: absolute; opacity: 0;"><ins class="iCheck-helper" style="position: absolute; top: 0%; left: 0%; display: block; width: 100%; height: 100%; margin: 0px; padding: 0px; background: rgb(255, 255, 255); border: 0px; opacity: 0;"></ins></div> Disabled
                              </label>
                </div>
                <div class="radio">
                  <label>
                                <div class="iradio_flat-green checked disabled" style="position: relative;"><input type="radio" class="flat" name="iCheck3" disabled="disabled" checked="" style="position: absolute; opacity: 0;"><ins class="iCheck-helper" style="position: absolute; top: 0%; left: 0%; display: block; width: 100%; height: 100%; margin: 0px; padding: 0px; background: rgb(255, 255, 255); border: 0px; opacity: 0;"></ins></div> Disabled &amp; Checked
                              </label>
                </div>
              </div>
            </div>
            <div class="form-group">
              <label class="control-label col-md-3 col-sm-3 col-xs-12">Switch</label>
              <div class="col-md-9 col-sm-9 col-xs-12">
                <div class="">
                  <label>
                                <input type="checkbox" class="js-switch" checked="" style="display: none;" data-switchery="true"><span class="switchery switchery-default" style="background-color: rgb(38, 185, 154); border-color: rgb(38, 185, 154); box-shadow: rgb(38, 185, 154) 0px 0px 0px 11px inset; transition: border 0.4s, box-shadow 0.4s, background-color 1.2s;"><small style="left: 12px; background-color: rgb(255, 255, 255); transition: background-color 0.4s, left 0.2s;"></small></span> Checked
                              </label>
                </div>
                <div class="">
                  <label>
                                <input type="checkbox" class="js-switch" data-switchery="true" style="display: none;"><span class="switchery switchery-default" style="box-shadow: rgb(223, 223, 223) 0px 0px 0px 0px inset; border-color: rgb(223, 223, 223); background-color: rgb(255, 255, 255); transition: border 0.4s, box-shadow 0.4s;"><small style="left: 0px; transition: background-color 0.4s, left 0.2s;"></small></span> Unchecked
                              </label>
                </div>
                <div class="">
                  <label>
                                <input type="checkbox" class="js-switch" disabled="disabled" data-switchery="true" readonly="" style="display: none;"><span class="switchery switchery-default" style="box-shadow: rgb(223, 223, 223) 0px 0px 0px 0px inset; border-color: rgb(223, 223, 223); background-color: rgb(255, 255, 255); transition: border 0.4s, box-shadow 0.4s; opacity: 0.5;"><small style="left: 0px; transition: background-color 0.4s, left 0.2s;"></small></span> Disabled
                              </label>
                </div>
                <div class="">
                  <label>
                                <input type="checkbox" class="js-switch" disabled="disabled" checked="checked" style="display: none;" data-switchery="true" readonly=""><span class="switchery switchery-default" style="background-color: rgb(38, 185, 154); border-color: rgb(38, 185, 154); box-shadow: rgb(38, 185, 154) 0px 0px 0px 11px inset; transition: border 0.4s, box-shadow 0.4s, background-color 1.2s; opacity: 0.5;"><small style="left: 12px; background-color: rgb(255, 255, 255); transition: background-color 0.4s, left 0.2s;"></small></span> Disabled Checked
                              </label>
                </div>
              </div>
            </div>


            <div class="ln_solid"></div>
            <div class="form-group">
              <div class="col-md-9 col-sm-9 col-xs-12 col-md-offset-3">
                <button type="submit" class="btn btn-primary">Cancel</button>
                <button type="submit" class="btn btn-success">Submit</button>
              </div>
            </div>

          </form>
        </div>
      </div>
    </div>


    <div class="col-md-6 col-sm-12 col-xs-12">
      <div class="x_panel">
        <div class="x_title">
          <h2>Form Buttons <small>Sessions</small></h2>
          <ul class="nav navbar-right panel_toolbox">
            <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
            </li>
            <li class="dropdown">
              <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false"><i class="fa fa-wrench"></i></a>
              <ul class="dropdown-menu" role="menu">
                <li><a href="#">Settings 1</a>
                </li>
                <li><a href="#">Settings 2</a>
                </li>
              </ul>
            </li>
            <li><a class="close-link"><i class="fa fa-close"></i></a>
            </li>
          </ul>
          <div class="clearfix"></div>
        </div>
        <div class="x_content">

          <form class="form-horizontal form-label-left">

            <div class="form-group">
              <label class="col-sm-3 control-label">Button addons</label>

              <div class="col-sm-9">
                <div class="input-group">
                  <span class="input-group-btn">
                                                <button type="button" class="btn btn-primary">Go!</button>
                                            </span>
                  <input type="text" class="form-control">
                </div>
                <div class="input-group">
                  <input type="text" class="form-control">
                  <span class="input-group-btn">
                                                <button type="button" class="btn btn-primary">Go!</button>
                                            </span>
                </div>
              </div>
            </div>
            <div class="divider-dashed"></div>

            <div class="form-group">
              <label class="col-sm-3 control-label">Button addons</label>

              <div class="col-sm-9">
                <div class="input-group">
                  <input type="text" class="form-control" aria-label="Text input with dropdown button">
                  <div class="input-group-btn">
                    <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-expanded="false">Action <span class="caret"></span>
                                </button>
                    <ul class="dropdown-menu dropdown-menu-right" role="menu">
                      <li><a href="#">Action</a>
                      </li>
                      <li><a href="#">Another action</a>
                      </li>
                      <li><a href="#">Something else here</a>
                      </li>
                      <li class="divider"></li>
                      <li><a href="#">Separated link</a>
                      </li>
                    </ul>
                  </div>
                  <!-- /btn-group -->
                </div>
                <div class="input-group">
                  <input type="text" class="form-control">
                  <span class="input-group-btn">
                                <button type="button" class="btn btn-primary">Go!</button>
                              </span>
                </div>
              </div>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>

  <div class="col-md-12 col-sm-12 col-xs-12">
    <div class="x_panel">
      <div class="x_title">
        <h2>Text areas<small>Sessions</small></h2>
        <ul class="nav navbar-right panel_toolbox">
          <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
          </li>
          <li class="dropdown">
            <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false"><i class="fa fa-wrench"></i></a>
            <ul class="dropdown-menu" role="menu">
              <li><a href="#">Settings 1</a>
              </li>
              <li><a href="#">Settings 2</a>
              </li>
            </ul>
          </li>
          <li><a class="close-link"><i class="fa fa-close"></i></a>
          </li>
        </ul>
        <div class="clearfix"></div>
      </div>
      <div class="x_content">
        <div id="alerts"></div>
        <div class="btn-toolbar editor" data-role="editor-toolbar" data-target="#editor">
          <div class="btn-group">
            <a class="btn dropdown-toggle" data-toggle="dropdown" title="" data-original-title="Font"><i class="fa fa-font"></i><b class="caret"></b></a>
            <ul class="dropdown-menu">
              <li><a data-edit="fontName Serif" style="font-family:'Serif'">Serif</a></li>
              <li><a data-edit="fontName Sans" style="font-family:'Sans'">Sans</a></li>
              <li><a data-edit="fontName Arial" style="font-family:'Arial'">Arial</a></li>
              <li><a data-edit="fontName Arial Black" style="font-family:'Arial Black'">Arial Black</a></li>
              <li><a data-edit="fontName Courier" style="font-family:'Courier'">Courier</a></li>
              <li><a data-edit="fontName Courier New" style="font-family:'Courier New'">Courier New</a></li>
              <li><a data-edit="fontName Comic Sans MS" style="font-family:'Comic Sans MS'">Comic Sans MS</a></li>
              <li><a data-edit="fontName Helvetica" style="font-family:'Helvetica'">Helvetica</a></li>
              <li><a data-edit="fontName Impact" style="font-family:'Impact'">Impact</a></li>
              <li><a data-edit="fontName Lucida Grande" style="font-family:'Lucida Grande'">Lucida Grande</a></li>
              <li><a data-edit="fontName Lucida Sans" style="font-family:'Lucida Sans'">Lucida Sans</a></li>
              <li><a data-edit="fontName Tahoma" style="font-family:'Tahoma'">Tahoma</a></li>
              <li><a data-edit="fontName Times" style="font-family:'Times'">Times</a></li>
              <li><a data-edit="fontName Times New Roman" style="font-family:'Times New Roman'">Times New Roman</a></li>
              <li><a data-edit="fontName Verdana" style="font-family:'Verdana'">Verdana</a></li>
            </ul>
          </div>

          <div class="btn-group">
            <a class="btn dropdown-toggle" data-toggle="dropdown" title="" data-original-title="Font Size"><i class="fa fa-text-height"></i>&nbsp;<b class="caret"></b></a>
            <ul class="dropdown-menu">
              <li>
                <a data-edit="fontSize 5">
                  <p style="font-size:17px">Huge</p>
                </a>
              </li>
              <li>
                <a data-edit="fontSize 3">
                  <p style="font-size:14px">Normal</p>
                </a>
              </li>
              <li>
                <a data-edit="fontSize 1">
                  <p style="font-size:11px">Small</p>
                </a>
              </li>
            </ul>
          </div>

          <div class="btn-group">
            <a class="btn" data-edit="bold" title="" data-original-title="Bold (Ctrl/Cmd+B)"><i class="fa fa-bold"></i></a>
            <a class="btn" data-edit="italic" title="" data-original-title="Italic (Ctrl/Cmd+I)"><i class="fa fa-italic"></i></a>
            <a class="btn" data-edit="strikethrough" title="" data-original-title="Strikethrough"><i class="fa fa-strikethrough"></i></a>
            <a class="btn" data-edit="underline" title="" data-original-title="Underline (Ctrl/Cmd+U)"><i class="fa fa-underline"></i></a>
          </div>

          <div class="btn-group">
            <a class="btn" data-edit="insertunorderedlist" title="" data-original-title="Bullet list"><i class="fa fa-list-ul"></i></a>
            <a class="btn" data-edit="insertorderedlist" title="" data-original-title="Number list"><i class="fa fa-list-ol"></i></a>
            <a class="btn" data-edit="outdent" title="" data-original-title="Reduce indent (Shift+Tab)"><i class="fa fa-dedent"></i></a>
            <a class="btn" data-edit="indent" title="" data-original-title="Indent (Tab)"><i class="fa fa-indent"></i></a>
          </div>

          <div class="btn-group">
            <a class="btn" data-edit="justifyleft" title="" data-original-title="Align Left (Ctrl/Cmd+L)"><i class="fa fa-align-left"></i></a>
            <a class="btn" data-edit="justifycenter" title="" data-original-title="Center (Ctrl/Cmd+E)"><i class="fa fa-align-center"></i></a>
            <a class="btn" data-edit="justifyright" title="" data-original-title="Align Right (Ctrl/Cmd+R)"><i class="fa fa-align-right"></i></a>
            <a class="btn" data-edit="justifyfull" title="" data-original-title="Justify (Ctrl/Cmd+J)"><i class="fa fa-align-justify"></i></a>
          </div>

          <div class="btn-group">
            <a class="btn dropdown-toggle" data-toggle="dropdown" title="" data-original-title="Hyperlink"><i class="fa fa-link"></i></a>
            <div class="dropdown-menu input-append">
              <input class="span2" placeholder="URL" type="text" data-edit="createLink">
              <button class="btn" type="button">Add</button>
            </div>
            <a class="btn" data-edit="unlink" title="" data-original-title="Remove Hyperlink"><i class="fa fa-cut"></i></a>
          </div>

          <div class="btn-group">
            <a class="btn" title="" id="pictureBtn" data-original-title="Insert picture (or just drag &amp; drop)"><i class="fa fa-picture-o"></i></a>
            <input type="file" data-role="magic-overlay" data-target="#pictureBtn" data-edit="insertImage" style="opacity: 0; position: absolute; top: 0px; left: 0px; width: 41px; height: 34px;">
          </div>

          <div class="btn-group">
            <a class="btn" data-edit="undo" title="" data-original-title="Undo (Ctrl/Cmd+Z)"><i class="fa fa-undo"></i></a>
            <a class="btn" data-edit="redo" title="" data-original-title="Redo (Ctrl/Cmd+Y)"><i class="fa fa-repeat"></i></a>
          </div>
        </div>

        <div id="editor" class="editor-wrapper placeholderText" contenteditable="true"></div>

        <textarea name="descr" id="descr" style="display:none;"></textarea>

        <br>

        <div class="ln_solid"></div>

        <div class="form-group">
          <label class="control-label col-md-3 col-sm-3 col-xs-12">Resizable Text area</label>
          <div class="col-md-9 col-sm-9 col-xs-12">
            <textarea class="resizable_textarea form-control" placeholder="This text area automatically resizes its height as you fill in more text courtesy of autosize-master it out..." style="overflow: hidden; word-wrap: break-word; resize: horizontal; height: 54px;"></textarea>
          </div>
        </div>
      </div>
    </div>
  </div>

  <div class="x_panel">
    <div class="x_title">
      <h2>Form Input Grid <small>form input </small></h2>
      <ul class="nav navbar-right panel_toolbox">
        <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
        </li>
        <li class="dropdown">
          <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false"><i class="fa fa-wrench"></i></a>
          <ul class="dropdown-menu" role="menu">
            <li><a href="#">Settings 1</a>
            </li>
            <li><a href="#">Settings 2</a>
            </li>
          </ul>
        </li>
        <li><a class="close-link"><i class="fa fa-close"></i></a>
        </li>
      </ul>
      <div class="clearfix"></div>
    </div>
    <div class="x_content">

      <div class="row">

        <div class="col-md-12 col-sm-12 col-xs-12 form-group">
          <input type="text" placeholder=".col-md-12" class="form-control">
        </div>

        <div class="col-md-6 col-sm-12 col-xs-12 form-group">
          <input type="text" placeholder=".col-md-6" class="form-control">
        </div>

        <div class="col-md-6 col-sm-12 col-xs-12 form-group">
          <input type="text" placeholder=".col-md-6" class="form-control">
        </div>


        <div class="col-md-4 col-sm-12 col-xs-12 form-group">
          <input type="text" placeholder=".col-md-4" class="form-control">
        </div>

        <div class="col-md-4 col-sm-12 col-xs-12 form-group">
          <input type="text" placeholder=".col-md-4" class="form-control">
        </div>

        <div class="col-md-4 col-sm-12 col-xs-12 form-group">
          <input type="text" placeholder=".col-md-4" class="form-control">
        </div>


        <div class="col-md-3 col-sm-12 col-xs-12 form-group">
          <input type="text" placeholder=".col-md-3" class="form-control">
        </div>

        <div class="col-md-3 col-sm-12 col-xs-12 form-group">
          <input type="text" placeholder=".col-md-3" class="form-control">
        </div>

        <div class="col-md-3 col-sm-12 col-xs-12 form-group">
          <input type="text" placeholder=".col-md-3" class="form-control">
        </div>

        <div class="col-md-3 col-sm-12 col-xs-12 form-group">
          <input type="text" placeholder=".col-md-3" class="form-control">
        </div>


        <div class="col-md-2 col-sm-12 col-xs-12 form-group">
          <input type="text" placeholder=".col-md-2" class="form-control">
        </div>

        <div class="col-md-2 col-sm-12 col-xs-12 form-group">
          <input type="text" placeholder=".col-md-2" class="form-control">
        </div>

        <div class="col-md-2 col-sm-12 col-xs-12 form-group">
          <input type="text" placeholder=".col-md-2" class="form-control">
        </div>

        <div class="col-md-2 col-sm-12 col-xs-12 form-group">
          <input type="text" placeholder=".col-md-2" class="form-control">
        </div>

        <div class="col-md-2 col-sm-12 col-xs-12 form-group">
          <input type="text" placeholder=".col-md-2" class="form-control">
        </div>

        <div class="col-md-2 col-sm-12 col-xs-12 form-group">
          <input type="text" placeholder=".col-md-2" class="form-control">
        </div>


        <div class="col-md-1 col-sm-12 col-xs-12 form-group">
          <input type="text" placeholder=".col-md-1" class="form-control">
        </div>

        <div class="col-md-1 col-sm-12 col-xs-12 form-group">
          <input type="text" placeholder=".col-md-1" class="form-control">
        </div>

        <div class="col-md-1 col-sm-12 col-xs-12 form-group">
          <input type="text" placeholder=".col-md-1" class="form-control">
        </div>

        <div class="col-md-1 col-sm-12 col-xs-12 form-group">
          <input type="text" placeholder=".col-md-1" class="form-control">
        </div>

        <div class="col-md-1 col-sm-12 col-xs-12 form-group">
          <input type="text" placeholder=".col-md-1" class="form-control">
        </div>

        <div class="col-md-1 col-sm-12 col-xs-12 form-group">
          <input type="text" placeholder=".col-md-1" class="form-control">
        </div>

        <div class="col-md-1 col-sm-12 col-xs-12 form-group">
          <input type="text" placeholder=".col-md-1" class="form-control">
        </div>

        <div class="col-md-1 col-sm-12 col-xs-12 form-group">
          <input type="text" placeholder=".col-md-1" class="form-control">
        </div>

        <div class="col-md-1 col-sm-12 col-xs-12 form-group">
          <input type="text" placeholder=".col-md-1" class="form-control">
        </div>

        <div class="col-md-1 col-sm-12 col-xs-12 form-group">
          <input type="text" placeholder=".col-md-1" class="form-control">
        </div>

        <div class="col-md-1 col-sm-12 col-xs-12 form-group">
          <input type="text" placeholder=".col-md-1" class="form-control">
        </div>

        <div class="col-md-1 col-sm-12 col-xs-12 form-group">
          <input type="text" placeholder=".col-md-1" class="form-control">
        </div>
      </div>

    </div>
  </div>


  <div class="x_panel">
    <div class="x_title">
      <h2>Form Design <small>different form elements</small></h2>
      <ul class="nav navbar-right panel_toolbox">
        <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
        </li>
        <li class="dropdown">
          <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false"><i class="fa fa-wrench"></i></a>
          <ul class="dropdown-menu" role="menu">
            <li><a href="#">Settings 1</a>
            </li>
            <li><a href="#">Settings 2</a>
            </li>
          </ul>
        </li>
        <li><a class="close-link"><i class="fa fa-close"></i></a>
        </li>
      </ul>
      <div class="clearfix"></div>
    </div>
    <div class="x_content">
      <br>

      <h4>Horizontal labels</h4>
      <p class="font-gray-dark">
        Using the grid system you can control the position of the labels. The form example below has the <b>col-md-10</b> which sets the width to 10/12 and <b>center-margin</b> which centers it.
      </p>
      <form class="form-horizontal form-label-left">
        <div class="form-group">
          <label class="control-label col-md-3" for="first-name">First Name <span class="required">*</span>
                      </label>
          <div class="col-md-7">
            <input type="text" id="first-name2" required="required" class="form-control col-md-7 col-xs-12">
          </div>
        </div>
        <div class="form-group">
          <label class="control-label col-md-3" for="last-name">Last Name <span class="required">*</span>
                      </label>
          <div class="col-md-7">
            <input type="text" id="last-name2" name="last-name" required="required" class="form-control col-md-7 col-xs-12">
          </div>
        </div>
      </form>


      <h4>Vertical labels</h4>
      <p class="font-gray-dark">
        For making labels vertical you have two options, either use the apropiate grid <b>.col-</b> class or wrap the form in the <b>form-vertical</b> class.
      </p>
      <div class="col-md-8 center-margin">
        <form class="form-horizontal form-label-left">
          <div class="form-group">
            <label>Email address</label>
            <input type="email" class="form-control" placeholder="Enter email">
          </div>
          <div class="form-group">
            <label>Password</label>
            <input type="password" class="form-control" placeholder="Password" style="background-image: url(&quot;data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAACIUlEQVQ4EX2TOYhTURSG87IMihDsjGghBhFBmHFDHLWwSqcikk4RRKJgk0KL7C8bMpWpZtIqNkEUl1ZCgs0wOo0SxiLMDApWlgOPrH7/5b2QkYwX7jvn/uc//zl3edZ4PPbNGvF4fC4ajR5VrNvt/mo0Gr1ZPOtfgWw2e9Lv9+chX7cs64CS4Oxg3o9GI7tUKv0Q5o1dAiTfCgQCLwnOkfQOu+oSLyJ2A783HA7vIPLGxX0TgVwud4HKn0nc7Pf7N6vV6oZHkkX8FPG3uMfgXC0Wi2vCg/poUKGGcagQI3k7k8mcp5slcGswGDwpl8tfwGJg3xB6Dvey8vz6oH4C3iXcFYjbwiDeo1KafafkC3NjK7iL5ESFGQEUF7Sg+ifZdDp9GnMF/KGmfBdT2HCwZ7TwtrBPC7rQaav6Iv48rqZwg+F+p8hOMBj0IbxfMdMBrW5pAVGV/ztINByENkU0t5BIJEKRSOQ3Aj+Z57iFs1R5NK3EQS6HQqF1zmQdzpFWq3W42WwOTAf1er1PF2USFlC+qxMvFAr3HcexWX+QX6lUvsKpkTyPSEXJkw6MQ4S38Ljdbi8rmM/nY+CvgNcQqdH6U/xrYK9t244jZv6ByUOSiDdIfgBZ12U6dHEHu9TpdIr8F0OP692CtzaW/a6y3y0Wx5kbFHvGuXzkgf0xhKnPzA4UTyaTB8Ph8AvcHi3fnsrZ7Wore02YViqVOrRXXPhfqP8j6MYlawoAAAAASUVORK5CYII=&quot;); background-repeat: no-repeat; background-attachment: scroll; background-size: 16px 18px; background-position: 98% 50%;">
          </div>

        </form>
      </div>

      <h4>Inline Form </h4>
      <p class="font-gray-dark">
        For making labels vertical you have two options, either use the apropiate grid <b>.col-</b> class or wrap the form in the <b>form-vertical</b> class.
      </p>
      <form class="form-inline">
        <div class="form-group">
          <label for="ex3">Email address</label>
          <input type="text" id="ex3" class="form-control" placeholder=" ">
        </div>
        <div class="form-group">
          <label for="ex4">Email address</label>
          <input type="email" id="ex4" class="form-control" placeholder=" ">
        </div>
        <div class="checkbox">
          <label>
                        <input type="checkbox"> Remember me
                      </label>
        </div>
        <button type="submit" class="btn btn-default">Send invitation</button>
      </form>
    </div>
  </div>
</div>
</template>

<script>
export default {
  name: 'Organisation',

  created() {
    this.fetchData(this.$route);
  },

  data() {
    return {
      title: 'Neue Organisation'
    };
  },

  methods: {
    fetchData($route) {

    }
  }
}
</script>

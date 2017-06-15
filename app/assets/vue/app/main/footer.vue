<template>
  <div class="footer-v1">
    <div class="footer">
      <div class="container">
        <div class="row">
          <!-- About -->
          <div class="col-md-3 md-margin-bottom-40">
            <a href="index.html"><img id="logo-footer" class="footer-logo" src="/assets/images/logo-small.png" alt=""></a>
          </div>
          <!--/col-md-3-->
          <!-- End About -->

          <!-- Link List -->
          <div class="col-md-3 col-md-offset-3 md-margin-bottom-40">
            <div class="headline">
              <h2>Unterstützen</h2>
            </div>
            <p>
              Profitieren Sie von den Vorteilen der Premiummitgliedschaft und unterstützen Sie gleichzeitig respondeco.
            </p>
            <img id="logo-premium" src="/assets/images/button_premiummitglied-werden.png"
                 alt="Werden Sie jetzt Premiummitglied">
          </div>
          <!--/col-md-3-->
          <!-- End Link List -->

          <!-- Address -->
          <div class="col-md-3 map-img md-margin-bottom-40">
            <div class="headline">
              <h2>Contact Us</h2></div>
            <address class="md-margin-bottom-40">
              Grünbergstraße 3/17 <br/>
              1120 Wien, Österreich <br/>
              E-Mail: info@respondeco.org
            </address>
          </div>
          <!--/col-md-3-->
          <!-- End Address -->
        </div>
      </div>
    </div>
    <!--/footer-->

    <div class="copyright">
      <div class="container">
        <div class="row">
          <div class="col-md-6">
            <p>
              2015 &copy; All Rights Reserved.
              <a href="#">Privacy Policy</a> | <a href="#">Terms of Service</a>
            </p>
          </div>

          <!-- Social Links -->
          <div class="col-md-6">
            <ul class="footer-socials list-inline">
              <li>
                <a href="#" class="tooltips" data-toggle="tooltip" data-placement="top" title=""
                   data-original-title="Facebook">
                  <i class="fa fa-facebook"></i>
                </a>
              </li>
              <li>
                <a href="#" class="tooltips" data-toggle="tooltip" data-placement="top" title=""
                   data-original-title="Skype">
                  <i class="fa fa-skype"></i>
                </a>
              </li>
              <li>
                <a href="#" class="tooltips" data-toggle="tooltip" data-placement="top" title=""
                   data-original-title="Google Plus">
                  <i class="fa fa-google-plus"></i>
                </a>
              </li>
              <li>
                <a href="#" class="tooltips" data-toggle="tooltip" data-placement="top" title=""
                   data-original-title="Linkedin">
                  <i class="fa fa-linkedin"></i>
                </a>
              </li>
              <li>
                <a href="#" class="tooltips" data-toggle="tooltip" data-placement="top" title=""
                   data-original-title="Pinterest">
                  <i class="fa fa-pinterest"></i>
                </a>
              </li>
              <li>
                <a href="#" class="tooltips" data-toggle="tooltip" data-placement="top" title=""
                   data-original-title="Twitter">
                  <i class="fa fa-twitter"></i>
                </a>
              </li>
              <li>
                <a href="#" class="tooltips" data-toggle="tooltip" data-placement="top" title=""
                   data-original-title="Dribbble">
                  <i class="fa fa-dribbble"></i>
                </a>
              </li>
            </ul>
          </div>
          <!-- End Social Links -->
        </div>
      </div>
    </div>
    <!--/copyright-->
  </div>
  <!--=== End Footer Version 1 ===-->
</template>

<script>
  export default {
    name: 'RespondecoFooter'
  }
</script>

<style>
  #logo-premium {
    margin-top: 50px;
    max-width: 250px;
  }
</style>

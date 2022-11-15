<% response.setStatus(403); %>
<%@ include file="/WEB-INF/jspf/htmlTags.jspf" %>
<!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8"/>
  <title>Forbidden (403)</title>
  <link rel="stylesheet" href="/webfiles/latest/assets/css/main.css">
  <link href='https://fonts.googleapis.com/css?family=Roboto:100,300,400,500,700,900,400italic' rel='stylesheet' type='text/css'>
</head>

<body>
  <div class="ds_page">

    <%@ include file="header.html" %>

    <div class="ds_page__middle">
      <div class="ds_wrapper">
        <main id="main-content" class="ds_layout  ds_layout--article">
          <div class="ds_layout__header">
            <header class="ds_page-header">
              <h1 class="ds_page-header__title">Forbidden (403)</h1>
            </header>
          </div>

          <div class="ds_layout__content">
            <p>
              Sorry, but you are not authorized to view this URL.
            </p>
          </div>
        </main>
      </div>
    </div>

    <%@ include file="footer.html" %>

  </div>
</body>

</html>

<?php
    include("uasniffer.php");
    
    function headerDisableCache() {
        header("Expires: Mon, 26 Jul 1997 05:00:00 GMT");             // Date in the past
        header("Last-Modified: " . gmdate("D, d M Y H:i:s") . " GMT");// always modified
        header("Cache-Control: no-store, no-cache, must-revalidate"); // HTTP/1.1
        header("Cache-Control: post-check=0, pre-check=0", false);    // HTTP/1.1
        header("Pragma: no-cache");                                   // HTTP/1.0
    }

    function parseheaders() {
        $accept = array();
        $headers = array();
        $x_headers = array();
        $devcap_headers = array();
        foreach (getallheaders() as $header_name => $header_value) {
            if ($header_name=='Accept') {
                $accept["'Accept'"] = $header_value;
            } elseif (substr_count($header_name, 'Accept-')) {
                $accept[str_replace('Accept-', '', $header_name)] = $header_value;
            } elseif (substr_count($header_name, 'x-up-devcap-')) {
                $devcap_headers[str_replace('x-up-devcap-', '', $header_name)] = $header_value;
            } elseif (substr_count($header_name, 'x-')) {
                $x_headers[str_replace('x-', '', $header_name)] = $header_value;
            } else {
                $headers[$header_name] = $header_value;
            }
        }
        if (!count($headers)) $headers=null;
        if (!count($accept)) $accept=null;
        if (!count($x_headers)) $x_headers=null;
        if (!count($devcap_headers)) $devcap_headers=null;
        return array($accept, $headers, $x_headers, $devcap_headers);
    }

    function header_section($name, $headers, $expand=0) {
        if ($headers==null) return "";
        $str = "<h1>$name</h1>\n";
        foreach ($headers as $name => $value) $str .= header_item($name, $value, $expand);
        return $str;
    }
    
    function header_item($name, $value, $expand) {
        return "<h2>$name</h2>\n".expand_value($value, $expand);
    }
    function expand_value($value, $depth) {
        $str = "";
        switch ($depth) {
        case 0:
            return "$value\n";
        case 1:
            foreach (explode(',', $value) as $expanded) {
                $v = expand_value($expanded, $depth-1);
                $str .= "<li>$v</li>\n";
            }
            $str = "<ul>$str</ul>\n";
            break;
        case 2:
            foreach (explode(";", $value) as $expanded) {
                $v = expand_value($expanded, $depth-1);
                $str .= "$v<hr />\n";
            }
            break;
        default:
            return "Invalid expansion lavel: $depth";
        }

        return $str;
    }

    function uainfo() {
        $ua = sniffua();
        if (!count($ua)) return "";
        return "<h1>[User Agent Info]</h1>".
               "<h2>Model</h2>{$ua['MODEL']}".
               "<h2>UA Signature</h2>{$ua['MODEL_UA_SIG']}".
               "<h2>Browser</h2>{$ua['BROWSER']}".
               "<h2>Server</h2>{$ua['SERVER']}".
               "<h2>EzAppli</h2>{$ua['APPLI']}";
    }
    
    
    headerDisableCache();
    header('Content-type: text/html; charset=shift_jis', true);
    header('Content-type: application/xhtml+xml; charset=shift_jis', true);

    list($accept, $headers, $x_headers, $devcap) = parseheaders();
    $body .= uainfo();
    $body .= header_section("[Common Headers]", $headers);
    $body .= header_section("x-*", $x_headers);
    $body .= header_section("x-up-devcap-*", $devcap);
    $body .= header_section("Accept-*", $accept, 2);
    
?>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML Basic1.0//EN" "http://www.w3.org/TR/xhtml-basic/xhtml-basic10.dtd">
<html>
<head>
    <link href="headers.css" rel="stylesheet" type="text/css"/>
    <title>HTTP Headers</title>
</head>
<body>
<?= $body ?>
</body>
</html>
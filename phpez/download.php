<?php
    function headerDisableCache() {
        header("Expires: Mon, 26 Jul 1997 05:00:00 GMT");             // Date in the past
        header("Last-Modified: " . gmdate("D, d M Y H:i:s") . " GMT");// always modified
        header("Cache-Control: no-store, no-cache, must-revalidate"); // HTTP/1.1
        header("Cache-Control: post-check=0, pre-check=0", false);    // HTTP/1.1
        header("Pragma: no-cache");                                   // HTTP/1.0
    }

    function parseEnvironment() {
        $script_uri = parse_url($_SERVER['SCRIPT_URI']);
        $_dirpath = explode('/', $script_uri['path']);
        array_pop($_dirpath);
        $_dirpath = implode($_dirpath, '/');
        
        $baseurl = array_key_exists('scheme', $script_uri) ? (    $script_uri['scheme'].'://') : '';
        $baseurl.= array_key_exists('user'  , $script_uri) ? (    $script_uri['user'  ].'@'  ) : '';
        $baseurl.=                                                $script_uri['host'  ];
        $baseurl.= array_key_exists('port'  , $script_uri) ? (':'.$script_uri['port'  ]) : '';
        $baseurl.=                                                $_dirpath;
        
        $title = $script_uri['host'].'<br/>'.$_dirpath;
        return array($baseurl, $title);
    }
    
    function parseRequest() {
        $filter = $_REQUEST['filter'];
        $style = (array_key_exists('style', $_REQUEST) && ($_REQUEST['style']=='menu'))
            ? "menu"
            : "numbered";
        $disposition = (array_key_exists('disposition', $_REQUEST))
            ? $_REQUEST['disposition']
            : 'devkdjx'; // for phase 3 it's devkdj3
        return array($filter, $style, $disposition);
    }

    function appliLink($filename, $disposition) {
        $filepath=pathinfo($filename);
        $name = explode('.', $filepath["basename"]);
        $name = $name[0];

        if ($filepath['extension']!='kjx') return null;
        if ($filter!='' && !substr_count($name, $filter)) return null;

        $agemodified = intval(time() - filemtime($filename));
        $agemodified = intval($agemodified / 60);
        $age = ":".str_pad(intval($agemodified%60), 2, "0", STR_PAD_LEFT);  $agemodified=intval($agemodified / 60);
        $age = str_pad(intval($agemodified%24), 2, "0", STR_PAD_LEFT).$age; $agemodified=intval($agemodified / 60);
        if ($agemodified>0) $age = "$agemodified days";
        
        $filesize = filesize($filename);
        return  "<object data='$filename' type='application/x-kjx' standby='$name [$age]'>".
                "<param name='title' value='$name' valuetype='data'/>".
                "<param name='size' value='$filesize' valuetype='data'/>".
                "<param name='disposition' value='$disposition' valuetype='data' />".
                "</object>";
    }

    function appliList($dir, $disposition, $style) {
        $appli_list = "";
    
        clearstatcache();
        $handler = opendir($dir);
        while ($filename = readdir($handler)) {
            $entry = appliLink($filename, $disposition);
            if ($entry==null) continue;
            
            $zebra = 1 - $zebra;
            if ($zebra) $divclass = "zebra-light"; else  $divclass = "zebra-dark";
            $entry = "<div class='$divclass'>$entry</div>";
    
            if ($style=="numbered") $entry = "<li>$entry</li>";
            
            $appli_list .= $entry."\n";
        }
        closedir($handler);
    
        if ($style=="numbered") $appli_list = "<ol>\n$appli_list</ol>\n";
    
        return $appli_list;
    }
    
    headerDisableCache();
    header('Content-type: text/html; charset=shift_jis', true);
//    header('Content-type: application/xhtml+xml; charset=shift_jis', true);

    list($baseurl, $title) = parseEnvironment();
    list($filter, $style, $disposition) = parseRequest();
?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML Basic1.0//EN" "http://www.w3.org/TR/xhtml-basic/xhtml-basic10.dtd">
<html>
<head>
    <base href="<?= $baseurl ?>"/>
    <link href="<?= $baseurl ?>/download.css" rel="stylesheet" type="text/css"/>
    <title>Download Appli</title>
</head>
<body>
<h1>UA</h1>
<?= uainfo() ?>
<?= appliList('.', $disposition, $style) ?>
</body>
</html>

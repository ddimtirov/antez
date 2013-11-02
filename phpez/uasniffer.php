<?php
// For keitai user agent list see:  http://www.au.kddi.com/ezfactory/tec/spec/4_4.html

function sniffua($uaheader=null) {
    global $UA2MODEL, $MODEL2APPLI;
    
    if ($uaheader==null) {
        $allheaders = getallheaders();
        $uaheader = $allheaders['User-Agent'];
    }
    if (preg_match('/KDDI-(\w{4}) (.+) \(GUI\) (.+)/', $uaheader, $parsed)) {
        // SAMPLE: KDDI-HI21 UP. Browser/6.0.2.254 (GUI) MMP/1.1
        $desc = array(
            'MODEL_UA_SIG'  => $parsed[1],
            'BROWSER'       => $parsed[2],
            'SERVER'        => $parsed[3],
            'MODEL'         => $UA2MODEL[$parsed[1]]
        );
    } else if (preg_match('/(.+)-(\w{4}) (.+)/', $uaheader, $parsed)) {
        // SAMPLE: UP. Browser/3.01-HI02 UP.Link/3.2.1.2
        $desc = array(
            'BROWSER'       => $parsed[1],
            'MODEL_UA_SIG'  => $parsed[0],
            'SERVER'        => $parsed[3],
            'MODEL'         => $UA2MODEL[$parsed[2]]
        );
    } else {
        return array();
    }
    
    $desc['APPLI'] = array_key_exists($desc['MODEL'], $MODEL2APPLI)
        ? $MODEL2APPLI[$desc['MODEL']]
        : 'n/a';
        
    return $desc;
}


$UA2MODEL = array(
    'SA31' => 'W21SA',      'KC32' => 'W21K',       'KC31' => 'W11K',       'SN31' => 'W21S',
    'HI32' => 'W21H',       'HI31' => 'W11H',       'ST22' => 'INFOBAR',    'SA27' => 'A5505SA',
    'SA26' => 'A5503SA',    'TS26' => 'A5501T',     'CA25' => 'A5406CA',    'SN25' => 'A5404S',
    'SN24' => 'A5402S',     'CA23' => 'A5401CA',    'KC22' => 'A5305K',     'HI24' => 'A5303H II',
    'CA22' => 'A5302CA',    'TS21' => 'C5001T',     'TS28' => 'A5506T',     'TS27' => 'A5504T',
    'KC24' => 'A5502K',     'KC25' => 'A5502K',     'CA26' => 'A5407CA',    'ST23' => 'A5405SA',
    'CA24' => 'A5403CA',    'CA23' => 'A5401CA II', 'ST21' => 'A5306ST',    'TS24' => 'A5304T',
    'HI23' => 'A5303H',     'TS23' => 'A5301T',     'SN27' => 'A1402S II',  'SN26' => 'A1402S',
    'SA28' => 'A1305SA',    'TS25' => 'A1304T',     'SA24' => 'A1302SA',    'SN22' => 'A1101S',
    'SN28' => 'A1402S II',  'KC23' => 'A1401K',     'TS25' => 'A1304T II',  'SA25' => 'A1303SA',
    'SN23' => 'A1301S',     'SA22' => 'A3015SA',    'TS22' => 'A3013T',     'SA21' => 'A3011SA',
    'KC21' => 'C3002K',     'SN21' => 'A3014S',     'CA21' => 'A3012CA',    'MA21' => 'C3003P',
    'HI21' => 'C3001H',     'ST14' => 'A1014ST',    'KC14' => 'A1012K',     'SN17' => 'C1002S',
    'CA14' => 'C452CA',     'TS14' => 'C415T',      'SN15' => 'C413S',      'SN16' => 'C413S',
    'ST12' => 'C411ST',     'CA13' => 'C409CA',     'HI13' => 'C407H',      'SY13' => 'C405SA',
    'ST11' => 'C403ST',     'SY12' => 'C401SA',     'CA12' => 'C311CA',     'HI12' => 'C309H',
    'KC11' => 'C307K',      'SY11' => 'C304SA',     'HI11' => 'C302H',      'DN01' => 'C202DE',
    'KC15' => 'A1013K',     'ST13' => 'A1011ST',    'SY15' => 'C1001SA',    'HI14' => 'C451H',
    'KC13' => 'C414K',      'SY14' => 'C412SA',     'TS13' => 'C410T',      'MA13' => 'C408P',
    'SN13' => 'C406S',      'SN12' => 'C404S',      'SN14' => 'C404S',      'DN11' => 'C402DE',
    'KC12' => 'C313K',      'TS12' => 'C310T',      'MA11' => 'C308P',      'MA12' => 'C308P',
    'SN11' => 'C305S',      'CA11' => 'C303CA',     'TS11' => 'C301T',      'HI01' => 'C201H',
    'HI02' => 'C201H',
    'KCU1' => 'TK41',   'KCTD' => 'TK40',   'TST7' => 'TT31',   'SYT4' => 'TS31',   'KCTA' => 'TK22',
    'KCT9' => 'TK21',   'TST4' => 'TT11',   'SYT3' => 'TS11',   'MIT1' => 'TD11',   'KCT6' => 'TK05',
    'KCT5' => 'TK04',   'SYT2' => 'TS02',   'TST2' => 'TT02',   'KCT1' => 'TK01',   'SYT1' => 'TSO1',
    'SYT5' => 'TS41',   'TST8' => 'TT32',   'KCTC' => 'TK31',   'KCTB' => 'TK23',   'TST6' => 'TT22',
    'TST5' => 'TT21',   'KCT8' => 'TK12',   'KCT7' => 'TK11',   'MAT3' => 'TP11',   'TST3' => 'TT03',
    'KCT4' => 'TK03',   'MAT1' => 'TP01',   'MAT2' => 'TP01',   'KCT2' => 'TK02',   'KCT3' => 'TK02',
    'TST1' => 'TT01'
);

$MODEL2APPLI = array(
    'A5405SA'   => 'BREW2.0',  'INFOBAR'   => 'BREW2.0',  'A5306T'    => 'BREW2.0',  'A5304T'   => 'BREW2.0',
    'A1304T II' => 'BREW2.0',  'A1304T'    => 'BREW2.0',
    'W21SA'     => 'BREW2.1',  'W21S'      => 'BREW2.1',  'W21K'      => 'BREW2.1',  'A5506T'   => 'BREW2.1',
    'A5505SA'   => 'BREW2.1',  'A5504T'    => 'BREW2.1',  'A5503SA'   => 'BREW2.1',  'A5502K'   => 'BREW2.1',
    'A5501T'    => 'BREW2.1',  'A1402S II' => 'BREW2.1',  'A1402S II' => 'BREW2.1',  'A1402S'   => 'BREW2.1',
    'C452CA'    => 'PHASE1',   'C451H'     => 'PHASE1',   
    'C3001H'    => 'PHASE2',   'C3002K'    => 'PHASE2',   'C5001T'    => 'PHASE2',   'A3011SA'  => 'PHASE2',
    'A3012CA'   => 'PHASE2',   'A3013T'    => 'PHASE2',   'A3014S'    => 'PHASE2',   'PHASE2.5' => 'PHASE2.5',
    'A3015SA'   => 'PHASE2.5', 'A5301T'    => 'PHASE2.5', 'A5302CA'   => 'PHASE2.5', 'A5303H'   => 'PHASE2.5',
    'A5303H II' => 'PHASE2.5', 'A5305K'    => 'PHASE2.5', 'A5401CA'   => 'PHASE2.5', 'A5402S'   => 'PHASE2.5',
    'A5404S'    => 'PHASE2.5', 
    'A5403CA'   => 'PHASE3',   'A5406CA'   => 'PHASE3',   'A5407CA'   => 'PHASE3',   'W11H'     => 'PHASE3',
    'W11K'      => 'PHASE3'
);

?>
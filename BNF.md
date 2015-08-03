# Introduction #

Aviator的BNF文法


# Details #

ternary    ->     bool     `|`   bool? ternary : ternary

bool       ->     join     `|`   bool `|``|` join

join       ->     equality `|`   join && equality

equality   ->     rel      `|`   equality == rel `|`  equality =~ rel  `|` equality!= rel

rel        ->     expr     `|`    rel <= expr    `|`   rel < expr      `|` rel >= expr  `|` rel > expr

expr       ->     term    `|`     expr + term    `|`   expr-term

term       ->      unary  `|`     term `*` unary   `|`  term/unary

unary     ->      factor  `|`   !factor  `|` -factor

factor    ->       e      `|`    number  `|`   String   `|` variable   `|`  ( ternary ) `|`  / pattern / `|`   method  `|`  variable`[`ternary`]`

pattern   -> char list

method  ->  variable(paramlist)

paramlist -> ternary | paramlist,ternary

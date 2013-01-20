Turmites
========
A simple _N_-state two-color Turing machine which operates on a wrapped 
grid of black and white cells, and follows custom rules on every tick of 
a clock.

See http://turmites.destructuring-bind.org for a running example.
By default one ant is automatically created; Extra ants may be added
by clicking on the canvas. **PLEASE NOTE:** _For some reason, neither
Chromium v22 or Google Chrome v23 on Ubuntu 12.10 works properly when
rendering to the HTML canvas._ 

Rules may be specified by adding a `rule=...` parameter, of which some 
permissible values are shown below. 

* [langtons-ant](http://turmites.destructuring-bind.org/?rule=langtons-ant) (which has an implicit rule of 120080)

* [coiled-rope](http://turmites.destructuring-bind.org/?rule=coiled-rope) (implicit rule: 111180121010)

* [computer-art](http://turmites.destructuring-bind.org/?rule=computer-art) (implicit rule: 180121020081)

* [fibonacci](http://turmites.destructuring-bind.org/?rule=fibonacci) (implicit rule: 181181121010)

* [worm-trails](http://turmites.destructuring-bind.org/?rule=worm-trails) (implicit rule: 121181121020)

* [striped-spiral](http://turmites.destructuring-bind.org/?rule=striped-spiral) (implicit rule: 021080181020)

A full list of pre-defined rules can be found [here](https://github.com/rm-hull/turmites/blob/master/src/turmites/client/core.cljs#L33).
If no rule is given, then a pre-defined one will be selected at random.
Or, alternatively, the rule definitions may be specified as a sequence 
of numeric digits, eg. http://turmites.destructuring-bind.org/?rule=021080181020

An _N_-state two-color rule consists of _2N_ triples, _{a,b,c}_, where:

* _a_: the new color of the square

* _b_: the direction(s) for the turmite to turn

* _c_: the new internal state of the turmite

For example, the fibonacci turmite, has rule definition _181181121010_ has 
two states and two colors, and is split into four triples {1,8,1}, {1,8,1},
{1,2,1} and {0,1,0}. The particulat triple is selected based on the current
state and the current color. The {1,8,1} triple therefore states that the 
colour should change to 1, turn left (8) and adopt state 1 before moving 
forwards.

The direction to turn is specified by: 

* 1: no turn

* 2: right

* 4: u-turn

* 8: left

The actions are specified by a state transition table listing the current 
internal state of the turmite and the color of the cell it is currently 
standing on. For example, the next rule-set to use for the fibonacci triples
shown above are used by the following table:

                              current-color = 0    current-color = 1
    current-state = 0              1,8,1                1,8,1
    current-state = 1              1,2,1                0,1,0
    
So if the current state is 1 and the current color is 0, then the {1,2,1} 
triple is selected and the new color, direction and state are derived from
that.

Building and Running
--------------------
Invoke a noir instance using leiningen:

    $ lein run

The clojure script will compiled down to javascript as part of the build,
so browse to [http://localhost:8080](http://localhost:8080)

Editing Code
------------
Don't try to edit the javascript - it is compiled from ClojureScript 
in `src/turmites/client/core.cljs` using Leiningen with the lein-cljsbuild 
plugin:

    $ lein cljsbuild clean
    $ lein cljsbuild auto

Refresh the page in the browser after having saved the file and allow
for the compilation phase to complete.

Notes
-----
Turmites are a generalization of [Langtons-ants](https://github.com/rm-hull/langtons-ants). 

Inspired (in part) by Stephen Wolfram's, "A New Kind of Science", ISBN 1-57955-008-8

Neither **Chromium 22.0.1229.94** nor **Google Chrome 23.0.1271.97** (on Ubuntu) 
yield the expected output, whereas **Firefox 20.0a1** does. This might be down to 
any of the following

* the linux fglrx graphics drivers, 

* the chocolate factory's implementation of _Canvas.getImageData()_. 

* something else

I've seen similar problems on the Nexus 7 tablet too, but wouldn't like to hazard a guess
exactly where the problem is... **Chrome 23.x.xxxx.xx** (on Windows 7) _does_ work correctly,
as does **Safari** on OS X and iPad. 

References
----------
* http://www.maa.org/editorial/mathgames/mathgames_06_07_04.html

* http://code.google.com/p/ruletablerepository/wiki/TwoDimensionalTuringMachines

* http://demonstrations.wolfram.com/Turmites/

* https://en.wikipedia.org/wiki/Turmite 

* http://golly.sourceforge.net/

License
-------
Copyright (c) Richard Hull 2012

Same as the eclipse public license - v 1.0: https://www.eclipse.org/legal/epl-v10.html

Turmites
========
A simple _N_-state two-color Turing machine which operates on a wrapped 
grid of black and white cells, and follows custom rules on every tick of 
a clock.

See http://turmites.destructuring-bind.org for a running example.
By default one ant is automatically created; Extra ants may be added
by clicking on the canvas.

Rules may be specified by adding a `rule=...` parameter, of which some 
permissible values are shown below. 

* [langtons-ant](http://turmites.destructuring-bind.org?rule=langtons-ant) (which has an implicit rule of 120080)

* [coiled-rope](http://turmites.destructuring-bind.org?rule=coiled-rope) (implicit rule: 111180121010)

* [computer-art](http://turmites.destructuring-bind.org?rule=computer-art) (implicit rule: 180121020081)

* [fibonacci](http://turmites.destructuring-bind.org?rule=fibonacci) (implicit rule: 181181121010)

* [worm-trails](http://turmites.destructuring-bind.org?rule=worm-trails) (implicit rule: 121181121020)

A full list of pre-defined rules can be found [here](https://github.com/rm-hull/turmites/blob/master/src/turmites/core.cljs#L20).
If no rule is given, then a pre-defined one will be selected at random.
Or, alternatively, the rule definitions may be specified as a sequence 
of numeric digits.

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

Building and Running
--------------------
The app runs inside a static lightweight WEBrick instance (the app is
nominally deployed on Heroku), so Ruby and bundle are pre-requisites:

    $ sudo apt-get install bundle
    $ bundle install

The compiled javascript is already versioned in git, so start the 
appserver and browse to http://localhost:9292

    $ rackup

Editing Code
------------
Don't try to edit the javascript - it is compiled from ClojureScript 
in `src/ants/core.cljs` using Leiningen with the lein-cljsbuild plugin:

    $ lein cljsbuild clean
    $ lein cljsbuild auto

Refresh the page in the browser after having saved the file and allow
for the compilation phase to complete.

Notes
-----
Turmites are a generalization of [Langtons-ants](https://github.com/rm-hull/langtons-ants). 

Inspired (in part) by Stephen Wolfram's, "A New Kind of Science", ISBN 1-57955-008-8

Chromium 22.0.1229.94 (on Ubuntu) does not yield the expected output, whereas
Firefox 20.0a1 does. This might be down to Chromium's implementation of 
_Canvas.getImageData()_. Chrome 23.x.xxxx.xx (on Windows 7) does work correctly.

References
----------
* http://www.maa.org/editorial/mathgames/mathgames_06_07_04.html

* http://code.google.com/p/ruletablerepository/wiki/TwoDimensionalTuringMachines

* http://demonstrations.wolfram.com/Turmites/

* https://en.wikipedia.org/wiki/Turmite 

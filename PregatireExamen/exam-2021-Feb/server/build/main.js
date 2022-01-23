require('source-map-support/register');
module.exports =
/******/ (function(modules) { // webpackBootstrap
/******/ 	// The module cache
/******/ 	var installedModules = {};
/******/
/******/ 	// The require function
/******/ 	function __webpack_require__(moduleId) {
/******/
/******/ 		// Check if module is in cache
/******/ 		if(installedModules[moduleId]) {
/******/ 			return installedModules[moduleId].exports;
/******/ 		}
/******/ 		// Create a new module (and put it into the cache)
/******/ 		var module = installedModules[moduleId] = {
/******/ 			i: moduleId,
/******/ 			l: false,
/******/ 			exports: {}
/******/ 		};
/******/
/******/ 		// Execute the module function
/******/ 		modules[moduleId].call(module.exports, module, module.exports, __webpack_require__);
/******/
/******/ 		// Flag the module as loaded
/******/ 		module.l = true;
/******/
/******/ 		// Return the exports of the module
/******/ 		return module.exports;
/******/ 	}
/******/
/******/
/******/ 	// expose the modules object (__webpack_modules__)
/******/ 	__webpack_require__.m = modules;
/******/
/******/ 	// expose the module cache
/******/ 	__webpack_require__.c = installedModules;
/******/
/******/ 	// define getter function for harmony exports
/******/ 	__webpack_require__.d = function(exports, name, getter) {
/******/ 		if(!__webpack_require__.o(exports, name)) {
/******/ 			Object.defineProperty(exports, name, { enumerable: true, get: getter });
/******/ 		}
/******/ 	};
/******/
/******/ 	// define __esModule on exports
/******/ 	__webpack_require__.r = function(exports) {
/******/ 		if(typeof Symbol !== 'undefined' && Symbol.toStringTag) {
/******/ 			Object.defineProperty(exports, Symbol.toStringTag, { value: 'Module' });
/******/ 		}
/******/ 		Object.defineProperty(exports, '__esModule', { value: true });
/******/ 	};
/******/
/******/ 	// create a fake namespace object
/******/ 	// mode & 1: value is a module id, require it
/******/ 	// mode & 2: merge all properties of value into the ns
/******/ 	// mode & 4: return value when already ns object
/******/ 	// mode & 8|1: behave like require
/******/ 	__webpack_require__.t = function(value, mode) {
/******/ 		if(mode & 1) value = __webpack_require__(value);
/******/ 		if(mode & 8) return value;
/******/ 		if((mode & 4) && typeof value === 'object' && value && value.__esModule) return value;
/******/ 		var ns = Object.create(null);
/******/ 		__webpack_require__.r(ns);
/******/ 		Object.defineProperty(ns, 'default', { enumerable: true, value: value });
/******/ 		if(mode & 2 && typeof value != 'string') for(var key in value) __webpack_require__.d(ns, key, function(key) { return value[key]; }.bind(null, key));
/******/ 		return ns;
/******/ 	};
/******/
/******/ 	// getDefaultExport function for compatibility with non-harmony modules
/******/ 	__webpack_require__.n = function(module) {
/******/ 		var getter = module && module.__esModule ?
/******/ 			function getDefault() { return module['default']; } :
/******/ 			function getModuleExports() { return module; };
/******/ 		__webpack_require__.d(getter, 'a', getter);
/******/ 		return getter;
/******/ 	};
/******/
/******/ 	// Object.prototype.hasOwnProperty.call
/******/ 	__webpack_require__.o = function(object, property) { return Object.prototype.hasOwnProperty.call(object, property); };
/******/
/******/ 	// __webpack_public_path__
/******/ 	__webpack_require__.p = "/";
/******/
/******/
/******/ 	// Load entry module and return exports
/******/ 	return __webpack_require__(__webpack_require__.s = 0);
/******/ })
/************************************************************************/
/******/ ({

/***/ "./src/index.js":
/*!**********************!*\
  !*** ./src/index.js ***!
  \**********************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

function _toConsumableArray(arr) { return _arrayWithoutHoles(arr) || _iterableToArray(arr) || _unsupportedIterableToArray(arr) || _nonIterableSpread(); }

function _nonIterableSpread() { throw new TypeError("Invalid attempt to spread non-iterable instance.\nIn order to be iterable, non-array objects must have a [Symbol.iterator]() method."); }

function _unsupportedIterableToArray(o, minLen) { if (!o) return; if (typeof o === "string") return _arrayLikeToArray(o, minLen); var n = Object.prototype.toString.call(o).slice(8, -1); if (n === "Object" && o.constructor) n = o.constructor.name; if (n === "Map" || n === "Set") return Array.from(o); if (n === "Arguments" || /^(?:Ui|I)nt(?:8|16|32)(?:Clamped)?Array$/.test(n)) return _arrayLikeToArray(o, minLen); }

function _iterableToArray(iter) { if (typeof Symbol !== "undefined" && iter[Symbol.iterator] != null || iter["@@iterator"] != null) return Array.from(iter); }

function _arrayWithoutHoles(arr) { if (Array.isArray(arr)) return _arrayLikeToArray(arr); }

function _arrayLikeToArray(arr, len) { if (len == null || len > arr.length) len = arr.length; for (var i = 0, arr2 = new Array(len); i < len; i++) { arr2[i] = arr[i]; } return arr2; }

var koa = __webpack_require__(/*! koa */ "koa");

var app = module.exports = new koa();

var server = __webpack_require__(/*! http */ "http").createServer(app.callback());

var WebSocket = __webpack_require__(/*! ws */ "ws");

var wss = new WebSocket.Server({
  server: server
});

var Router = __webpack_require__(/*! koa-router */ "koa-router");

var cors = __webpack_require__(/*! @koa/cors */ "@koa/cors");

var bodyParser = __webpack_require__(/*! koa-bodyparser */ "koa-bodyparser");

app.use(bodyParser());
app.use(cors());
app.use(middleware);

function middleware(ctx, next) {
  var start = new Date();
  return next().then(function () {
    var ms = new Date() - start;
    console.log("".concat(start.toLocaleTimeString(), " ").concat(ctx.request.method, " ").concat(ctx.request.url, " ").concat(ctx.response.status, " - ").concat(ms, "ms"));
  });
}

var getRandomInt = function getRandomInt(min, max) {
  min = Math.ceil(min);
  max = Math.floor(max);
  return Math.floor(Math.random() * (max - min)) + min;
};

var names = ['IMGROOT', 'HAVA PUG', 'Y U CRYN', 'BOY', 'EXPIRED', 'GO FOXES', 'LLWLWWW'];
var statuses = ['new', 'working', 'damaged', 'old'];
var drivers = ['Michael', 'Matthew', 'Mason', 'Maverick', 'Miles', 'Maxwell', 'Max'];
var colors = ['Yellow', 'Blue', 'Black', 'Green', 'White', 'Red'];
var vehicles = [];

for (var i = 0; i < 50; i++) {
  vehicles.push({
    id: i + 1,
    license: names[getRandomInt(0, names.length)] + " " + i,
    status: statuses[getRandomInt(0, statuses.length)],
    seats: getRandomInt(2, 20),
    driver: drivers[getRandomInt(0, drivers.length)],
    color: colors[getRandomInt(0, colors.length)],
    cargo: getRandomInt(0, 400)
  });
}

var router = new Router();
router.get('/all', function (ctx) {
  ctx.response.body = vehicles;
  ctx.response.status = 200;
});
router.get('/review', function (ctx) {
  ctx.response.body = vehicles;
  ctx.response.status = 200;
});
router.get('/colors', function (ctx) {
  ctx.response.body = _toConsumableArray(new Set(vehicles.map(function (obj) {
    return obj.color;
  })));
  ctx.response.status = 200;
});
router.get('/vehicles/:color', function (ctx) {
  var headers = ctx.params;
  var color = headers.color;

  if (typeof color !== 'undefined') {
    ctx.response.body = vehicles.filter(function (obj) {
      return obj.color == color;
    });
    ctx.response.status = 200;
  } else {
    console.log("Missing or invalid: color!");
    ctx.response.body = {
      text: 'Missing or invalid: color!'
    };
    ctx.response.status = 404;
  }
});
router.get('/driver/:name', function (ctx) {
  var headers = ctx.params;
  var name = headers.name;

  if (typeof name !== 'undefined') {
    ctx.response.body = vehicles.filter(function (obj) {
      return obj.driver == name;
    });
    ctx.response.status = 200;
  } else {
    console.log("Missing or invalid: driver name!");
    ctx.response.body = {
      text: 'Missing or invalid: driver name!'
    };
    ctx.response.status = 404;
  }
});

var broadcast = function broadcast(data) {
  return wss.clients.forEach(function (client) {
    if (client.readyState === WebSocket.OPEN) {
      client.send(JSON.stringify(data));
    }
  });
};

router.post('/vehicle', function (ctx) {
  // console.log("ctx: " + JSON.stringify(ctx));
  var headers = ctx.request.body; // console.log("body: " + JSON.stringify(headers));

  var license = headers.license;
  var seats = headers.seats;
  var driver = headers.driver;
  var color = headers.color;
  var cargo = headers.cargo;

  if (typeof license !== 'undefined' && typeof driver !== 'undefined' && typeof seats !== 'undefined' && color !== 'undefined' && cargo !== 'undefined') {
    var index = vehicles.findIndex(function (obj) {
      return obj.license == license;
    });

    if (index !== -1) {
      console.log("Vehicle already exists!");
      ctx.response.body = {
        text: 'Vehicle already exists!'
      };
      ctx.response.status = 404;
    } else {
      var maxId = Math.max.apply(Math, vehicles.map(function (obj) {
        return obj.id;
      })) + 1;
      var obj = {
        id: maxId,
        license: license,
        status: 'new',
        seats: seats,
        driver: driver,
        color: color,
        cargo: cargo
      }; // console.log("created: " + JSON.stringify(license));

      vehicles.push(obj);
      broadcast(obj);
      ctx.response.body = obj;
      ctx.response.status = 200;
    }
  } else {
    console.log("Missing or invalid fields!");
    ctx.response.body = {
      text: 'Missing or invalid fields!'
    };
    ctx.response.status = 404;
  }
});
router.del('/vehicle/:id', function (ctx) {
  // console.log("ctx: " + JSON.stringify(ctx));
  var headers = ctx.params; // console.log("body: " + JSON.stringify(headers));

  var id = headers.id;

  if (typeof id !== 'undefined') {
    var index = vehicles.findIndex(function (obj) {
      return obj.id == id;
    });

    if (index === -1) {
      console.log("No vehicle with id: " + id);
      ctx.response.body = {
        text: 'Invalid vehicle id'
      };
      ctx.response.status = 404;
    } else {
      var obj = vehicles[index]; // console.log("deleting: " + JSON.stringify(obj));

      vehicles.splice(index, 1);
      ctx.response.body = obj;
      ctx.response.status = 200;
    }
  } else {
    console.log("Missing or invalid fields!");
    ctx.response.body = {
      text: 'Id missing or invalid'
    };
    ctx.response.status = 404;
  }
});
app.use(router.routes());
app.use(router.allowedMethods());
server.listen(2021);

/***/ }),

/***/ 0:
/*!****************************!*\
  !*** multi ./src/index.js ***!
  \****************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

module.exports = __webpack_require__(/*! C:\Users\crist\Documents\MA\Mobile\PregatireExamen\exam-2021-Feb\server\src/index.js */"./src/index.js");


/***/ }),

/***/ "@koa/cors":
/*!****************************!*\
  !*** external "@koa/cors" ***!
  \****************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = require("@koa/cors");

/***/ }),

/***/ "http":
/*!***********************!*\
  !*** external "http" ***!
  \***********************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = require("http");

/***/ }),

/***/ "koa":
/*!**********************!*\
  !*** external "koa" ***!
  \**********************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = require("koa");

/***/ }),

/***/ "koa-bodyparser":
/*!*********************************!*\
  !*** external "koa-bodyparser" ***!
  \*********************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = require("koa-bodyparser");

/***/ }),

/***/ "koa-router":
/*!*****************************!*\
  !*** external "koa-router" ***!
  \*****************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = require("koa-router");

/***/ }),

/***/ "ws":
/*!*********************!*\
  !*** external "ws" ***!
  \*********************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = require("ws");

/***/ })

/******/ });
//# sourceMappingURL=main.map
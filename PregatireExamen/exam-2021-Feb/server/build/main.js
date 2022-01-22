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

var koa = __webpack_require__(/*! koa */ "koa");

var app = module.exports = new koa();

const server = __webpack_require__(/*! http */ "http").createServer(app.callback());

const WebSocket = __webpack_require__(/*! ws */ "ws");

const wss = new WebSocket.Server({
  server
});

const Router = __webpack_require__(/*! koa-router */ "koa-router");

const cors = __webpack_require__(/*! @koa/cors */ "@koa/cors");

const bodyParser = __webpack_require__(/*! koa-bodyparser */ "koa-bodyparser");

app.use(bodyParser());
app.use(cors());
app.use(middleware);

function middleware(ctx, next) {
  const start = new Date();
  return next().then(() => {
    const ms = new Date() - start;
    console.log(`${start.toLocaleTimeString()} ${ctx.request.method} ${ctx.request.url} ${ctx.response.status} - ${ms}ms`);
  });
}

const getRandomInt = (min, max) => {
  min = Math.ceil(min);
  max = Math.floor(max);
  return Math.floor(Math.random() * (max - min)) + min;
};

const names = ['IMGROOT', 'HAVA PUG', 'Y U CRYN', 'BOY', 'EXPIRED', 'GO FOXES', 'LLWLWWW'];
const statuses = ['new', 'working', 'damaged', 'old'];
const drivers = ['Michael', 'Matthew', 'Mason', 'Maverick', 'Miles', 'Maxwell', 'Max'];
const colors = ['Yellow', 'Blue', 'Black', 'Green', 'White', 'Red'];
const vehicles = [];

for (let i = 0; i < 50; i++) {
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

const router = new Router();
router.get('/all', ctx => {
  ctx.response.body = vehicles;
  ctx.response.status = 200;
});
router.get('/review', ctx => {
  ctx.response.body = vehicles;
  ctx.response.status = 200;
});
router.get('/colors', ctx => {
  ctx.response.body = [...new Set(vehicles.map(obj => obj.color))];
  ctx.response.status = 200;
});
router.get('/vehicles/:color', ctx => {
  const headers = ctx.params;
  const color = headers.color;

  if (typeof color !== 'undefined') {
    ctx.response.body = vehicles.filter(obj => obj.color == color);
    ctx.response.status = 200;
  } else {
    console.log("Missing or invalid: color!");
    ctx.response.body = {
      text: 'Missing or invalid: color!'
    };
    ctx.response.status = 404;
  }
});
router.get('/driver/:name', ctx => {
  const headers = ctx.params;
  const name = headers.name;

  if (typeof name !== 'undefined') {
    ctx.response.body = vehicles.filter(obj => obj.driver == name);
    ctx.response.status = 200;
  } else {
    console.log("Missing or invalid: driver name!");
    ctx.response.body = {
      text: 'Missing or invalid: driver name!'
    };
    ctx.response.status = 404;
  }
});

const broadcast = data => wss.clients.forEach(client => {
  if (client.readyState === WebSocket.OPEN) {
    client.send(JSON.stringify(data));
  }
});

router.post('/vehicle', ctx => {
  // console.log("ctx: " + JSON.stringify(ctx));
  const headers = ctx.request.body; // console.log("body: " + JSON.stringify(headers));

  const license = headers.license;
  const seats = headers.seats;
  const driver = headers.driver;
  const color = headers.color;
  const cargo = headers.cargo;

  if (typeof license !== 'undefined' && typeof driver !== 'undefined' && typeof seats !== 'undefined' && color !== 'undefined' && cargo !== 'undefined') {
    const index = vehicles.findIndex(obj => obj.license == license);

    if (index !== -1) {
      console.log("Vehicle already exists!");
      ctx.response.body = {
        text: 'Vehicle already exists!'
      };
      ctx.response.status = 404;
    } else {
      let maxId = Math.max.apply(Math, vehicles.map(function (obj) {
        return obj.id;
      })) + 1;
      let obj = {
        id: maxId,
        license,
        status: 'new',
        seats,
        driver,
        color,
        cargo
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
router.del('/vehicle/:id', ctx => {
  // console.log("ctx: " + JSON.stringify(ctx));
  const headers = ctx.params; // console.log("body: " + JSON.stringify(headers));

  const id = headers.id;

  if (typeof id !== 'undefined') {
    const index = vehicles.findIndex(obj => obj.id == id);

    if (index === -1) {
      console.log("No vehicle with id: " + id);
      ctx.response.body = {
        text: 'Invalid vehicle id'
      };
      ctx.response.status = 404;
    } else {
      let obj = vehicles[index]; // console.log("deleting: " + JSON.stringify(obj));

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
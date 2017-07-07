import {Injectable} from '@angular/core';
import {Observable} from "rxjs/Observable";
import {Scheduler} from "rxjs/Rx";
import {Subject} from "rxjs/Subject";
import {isDevMode} from '@angular/core';


@Injectable()
export class $WebSocket  {

  private reconnectAttempts = 0;
  private sendQueue = [];
  private onOpenCallbacks = [];
  private onMessageCallbacks = [];
  private onErrorCallbacks = [];
  private onCloseCallbacks = [];
  private readyStateConstants = {
    'CONNECTING': 0,
    'OPEN': 1,
    'CLOSING': 2,
    'CLOSED': 3,
    'RECONNECT_ABORTED': 4
  };
  private normalCloseCode = 1000;
  private reconnectableStatusCodes = [4000];
  private socket: WebSocket;
  private dataStream: Subject<any>;
  private internalConnectionState: number;
  private config: WebSocketConfig;
  private url: string;

  constructor(private protocols?:Array<string>) {
    if (isDevMode()) {
      this.url = "ws://localhost:8088/game";
    }else {
      this.url = "ws://cam.dynadrop.com:8088/game";
    }
    var match = new RegExp('wss?:\/\/').test(this.url);
    if (!match) {
      throw new Error('Invalid url provided');
    }
    this.config = { initialTimeout: 500, maxTimeout: 300000, reconnectIfNotNormalClose: true };
    this.dataStream = new Subject();
  }

  connect(force:boolean = false) {
    var self = this;
    if (force || !this.socket || this.socket.readyState !== this.readyStateConstants.OPEN) {
      self.socket =this.protocols ? new WebSocket(this.url, this.protocols) : new WebSocket(this.url);

      self.socket.onopen =(ev: Event) => {
        this.onOpenHandler(ev);
      };
      self.socket.onmessage = (ev: MessageEvent) => {
        self.onMessageHandler(ev);
        this.dataStream.next(ev);
      };
      this.socket.onclose = (ev: CloseEvent) => {
        self.onCloseHandler(ev);
      };

      this.socket.onerror = (ev: ErrorEvent) => {
        self.onErrorHandler(ev);
        this.dataStream.error(ev);
      };

    }
  }
  send(data) {
    var self = this;
    if (this.getReadyState() != this.readyStateConstants.OPEN &&this.getReadyState() != this.readyStateConstants.CONNECTING ){
      this.connect();
    }
    self.sendQueue.push({message: data});
    self.fireQueue();
  };

  getDataStream():Subject<any>{
    return this.dataStream;
  }

  onOpenHandler(event: Event) {
    this.reconnectAttempts = 0;
    this.notifyOpenCallbacks(event);
    this.fireQueue();
  };
  notifyOpenCallbacks(event) {
    for (let i = 0; i < this.onOpenCallbacks.length; i++) {
      this.onOpenCallbacks[i].call(this, event);
    }
  }
  fireQueue() {
    while (this.sendQueue.length && this.socket.readyState === this.readyStateConstants.OPEN) {
      var data = this.sendQueue.shift();

      this.socket.send(
        JSON.stringify(data.message)
      );
    }
  }

  notifyCloseCallbacks(event) {
    for (let i = 0; i < this.onCloseCallbacks.length; i++) {
      this.onCloseCallbacks[i].call(this, event);
    }
  }

  notifyErrorCallbacks(event) {
    for (var i = 0; i < this.onErrorCallbacks.length; i++) {
      this.onErrorCallbacks[i].call(this, event);
    }
  }

  onOpen(cb) {
    this.onOpenCallbacks.push(cb);
    return this;
  };

  onClose(cb) {
    this.onCloseCallbacks.push(cb);
    return this;
  }

  onError(cb) {
    this.onErrorCallbacks.push(cb);
    return this;
  };


  onMessage(callback, options) {
    this.onMessageCallbacks.push({
      fn: callback,
      pattern: options ? options.filter : undefined,
      autoApply: options ? options.autoApply : true
    });
    return this;
  }

  onMessageHandler(message: MessageEvent) {
    var pattern;
    var self = this;
    var currentCallback;
    for (var i = 0; i < self.onMessageCallbacks.length; i++) {
      currentCallback = self.onMessageCallbacks[i];
      currentCallback.fn.apply(self, [message]);
    }

  };

  onCloseHandler(event: CloseEvent) {
    this.notifyCloseCallbacks(event);
    if ((this.config.reconnectIfNotNormalClose && event.code !== this.normalCloseCode) || this.reconnectableStatusCodes.indexOf(event.code) > -1) {
      this.reconnect();
    } else {
      this.dataStream.complete();
    }
  };

  onErrorHandler(event) {
    this.notifyErrorCallbacks(event);
  };

  reconnect() {
    this.close(true);
    var backoffDelay = this.getBackoffDelay(++this.reconnectAttempts);
    var backoffDelaySeconds = backoffDelay / 1000;
    // console.log('Reconnecting in ' + backoffDelaySeconds + ' seconds');
    setTimeout( this.connect(), backoffDelay);
    return this;
  }

  close(force: boolean) {
    if (force || !this.socket.bufferedAmount) {
      this.socket.close();
    }
    return this;
  };

  // Exponential Backoff Formula by Prof. Douglas Thain
  // http://dthain.blogspot.co.uk/2009/02/exponential-backoff-in-distributed.html
  getBackoffDelay(attempt) {
    var R = Math.random() + 1;
    var T = this.config.initialTimeout;
    var F = 2;
    var N = attempt;
    var M = this.config.maxTimeout;

    return Math.floor(Math.min(R * T * Math.pow(F, N), M));
  };

  setInternalState(state) {
    if (Math.floor(state) !== state || state < 0 || state > 4) {
      throw new Error('state must be an integer between 0 and 4, got: ' + state);
    }

    this.internalConnectionState = state;

  }

  /**
   * Could be -1 if not initzialized yet
   * @returns {number}
   */
  getReadyState() {
    if (this.socket == null)
    {
      return -1;
    }
    return this.internalConnectionState || this.socket.readyState;
  }

}

export interface WebSocketConfig {
  initialTimeout:number;
  maxTimeout:number ;
  reconnectIfNotNormalClose: boolean
}

import { Component } from '@angular/core';

import { DialogRef, ModalComponent, CloseGuard } from 'angular2-modal';
import { BSModalContext } from 'angular2-modal/plugins/bootstrap';

export class PromotionModalContext extends BSModalContext {
  public num1: number;
  public num2: number;
  public piece: string;
}

/**
 * A Sample of how simple it is to create a new window, with its own injects.
 */
@Component({
  selector: 'modal-content',
  styles: [`
        .custom-modal-container {
            padding: 15px;
        }

        .custom-modal-header {
            background-color: #219161;
            color: #fff;
            -webkit-box-shadow: 0px 3px 5px 0px rgba(0,0,0,0.75);
            -moz-box-shadow: 0px 3px 5px 0px rgba(0,0,0,0.75);
            box-shadow: 0px 3px 5px 0px rgba(0,0,0,0.75);
            margin-top: -15px;
            margin-bottom: 40px;
        }
    `],
  template: `
        <div class="container-fluid custom-modal-container">
            <div class="row custom-modal-header">
                <div class="col-sm-12">
                    <h1>Promotion - Pick one</h1>
                </div>
            </div>
            <div class="row">
                <div class="col-xs-12">
                    <div class="jumbotron">
                        <span style="cursor: pointer; font-size: 6em;" (click)="pickPiece('Queen')">&#9819;</span>&nbsp;&nbsp;&nbsp;
                        <span style="cursor: pointer; font-size: 6em;" (click)="pickPiece('Knight')">&#9822;</span>&nbsp;&nbsp;&nbsp;
                        <span style="cursor: pointer; font-size: 6em;" (click)="pickPiece('Rook')">&#9820;</span>&nbsp;&nbsp;&nbsp;
                        <span style="cursor: pointer; font-size: 6em;" (click)="pickPiece('Bishop')">&#9821;</span>
                    </div>
                </div>
            </div>
        </div>`
})
export class PromotionModal implements CloseGuard, ModalComponent<PromotionModalContext> {
  context: PromotionModalContext;

  public wrongAnswer: boolean;

  constructor(public dialog: DialogRef<PromotionModalContext>) {
    this.context = dialog.context;
    this.wrongAnswer = true;
    dialog.setCloseGuard(this);
  }

  onKeyUp(value) {
    this.wrongAnswer = value != 5;
    this.dialog.close();
  }

  pickPiece(piece: string) {
    this.dialog.close(piece);
  }

  beforeDismiss(): boolean {
    return true;
  }

  beforeClose(): boolean {
    return false;
  }
}

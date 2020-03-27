import * as React from "react";
import algosdk from "algosdk";
import $ from "jquery";
import AlgorandClient from "../../services/algorandsdk";
import copy from "clipboard-copy";
import SuggestedFeeComponent from "./suggestedfeecomponent";

/**
 * This component is used to opt to receive and asset which means that the account sends a 0 amount of an asset to itself using a account mnemonic given as a prop
 * @props mnemonic: string -> account mnemonic
 *
 * @state amount: number -> store the amount to send
 * @state assetID: number -> store the asset id of the asset
 * @state txnId: string -> store transaction id after the transaction is complete
 * @state errorMessage: string -> store error message if there is any error during transaction
 *
 * @author [Sam Abbassi](https://github.com/wisthsb1519)
 */
export default class AsaOptinComponent extends React.Component {
    constructor(props) {
      super(props);
      this.state = {
        amount: 0,
        assetID: "",
        txnId: "",
        errorMessage: ""
      };
    }
  
    copyToClipboard = () => {
      copy(this.state.txnId);
    };

    // load assetID to send
    loadAssetID = event => {
        this.setState({
          assetID: event.target.value
        });
      };

    componentDidMount() {
      $('[data-toggle="tooltip"]').tooltip();
    }
  
    componentDidUpdate() {
      $('[data-toggle="tooltip"]').tooltip();
    }
  
    // validating the sender account and also sending the transaction
    sendTransaction = () => {
    //   
        let mnemonic = this.props.mnemonic;
        let recoveredAccount = algosdk.mnemonicToSecretKey(mnemonic);
  
        this.startTransaction(recoveredAccount).catch(e => {
          console.log(e);
        });
      }
      // starting transaction here with the account from
  startTransaction = async recoveredAccount => {
    //Get the relevant params from the algod
    try {
      let params = await AlgorandClient.getTransactionParams();
      let endRound = params.lastRound + parseInt(1000);
      let assetID = Number.parseInt(this.state.assetID);
      
    // create opt-in transaction
      let opttxn = algosdk.makeAssetTransferTxn(recoveredAccount.addr, recoveredAccount.addr, undefined, undefined,
        params.fee, 0, params.lastRound, endRound, undefined, params.genesishashb64, params.genID, assetID);

      // sign the transaction
    // let signedTxn = algosdk.signTransaction(txn, recoveredAccount.sk);
      let rawSignedTxn = opttxn.signTxn(recoveredAccount.sk);

      // sending the transaction
    //   AlgorandClient.sendRawTransaction(signedTxn.blob)
        await AlgorandClient.sendRawTransaction(rawSignedTxn)
        .then(tx => {
          console.log(tx);
          this.setState({
            txnId: tx.txId,
            amount: 0,
            note: ""
          });
        })
        .catch(err => {
          console.log(err);
          // setting the error text to state
          this.setState({ errorMessage: err.text });
        });
    } catch (e) {
      console.log(e);
    }
  };
  render() {
    return (
      <div>
        <div
          style={{ padding: "4em" }}
          className="rounded-lg shadow border bg-light p-4"
        >
          <div className="form-group text-center">
            <h3>Opt in to Receive an Asset</h3>
          </div>
          <div className="form-group">
          </div>
          <div className="form-group">
            <label>Asset ID</label>
            <input
              type="number"
              className="form-control col-md-6"
              id="assetID"
              placeholder="Enter Asset ID"
              value={this.state.assetID}
              onChange={this.loadAssetID}
            />
          </div>

          <div className="form-group">
            <SuggestedFeeComponent />
          </div>
          <div className="d-flex justify-content-around">
            <button
              type="button"
              className="btn btn-dark px-4 mt-2"
              onClick={this.sendTransaction}
            >
              Opt In
            </button>
          </div>
        </div>
        {this.state.txnId !== "" ? (
          <div className="rounded-lg shadow border bg-light p-4 mt-3">
            <div className="text-success font-weight-bold">
              Transaction Sent
            </div>
            <div className="text-secondary">
              <span className="font-weight-bold">txnId: </span>
              <span
                data-toggle="tooltip"
                data-placement="bottom"
                data-html="true"
                title={
                  "<div>Copy to clipboard</div><div>" +
                  this.state.txnId +
                  "</div>"
                }
                onClick={this.copyToClipboard}
              >
                {this.state.txnId.substr(0, 10)}...
                {this.state.txnId.substr(-4, 4)}
              </span>
            </div>
          </div>
        ) : null}
        {this.state.errorMessage !== "" ? (
          <div className="rounded-lg shadow border bg-light p-4 my-3">
            <div className="text-danger font-weight-bold">Error</div>
            <div className="text-secondary">
              <span className="font-weight-bold">message: </span>
              <span>{this.state.errorMessage}</span>
            </div>
          </div>
        ) : null}
      </div>
    );
  }
};
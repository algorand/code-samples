import * as React from "react";
import AsaOptinComponent from "../stateful/asaoptincomponent";

/**
 * This component contain transaction components.
 *
 * @author [Mitrasish Mukherjee](https://github.com/mmitrasish)
 */
const AssetsPage = () => {
  return (
    
    <div className="container-fluid row">
      <div className="col-md-6 d-flex justify-content-center">
        <div style={{ marginTop: "5em" }} className="col-md-8">
            <AsaOptinComponent mnemonic={localStorage.getItem("mnemonic")} />
        </div>
    </div>
    </div>
  );
};
export default AssetsPage;

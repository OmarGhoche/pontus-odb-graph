package com.pontusvision.gdpr;

import javax.script.Bindings;
import java.io.Serializable;

public class GremlinRequest implements Serializable {
  //    {
  //        searchStr: self.searchstr,
  //                from: from,
  //            to: to,
  //            sortBy: self.sortcol,
  //            sortDir: ((self.sortdir > 0) ? "+asc" : "+desc")
  //    }

  String gremlin;
  Bindings bindings;
  String requestId;

  public GremlinRequest() {
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof GremlinRequest))
      return false;

    GremlinRequest that = (GremlinRequest) o;

    if (gremlin != null ? !gremlin.equals(that.gremlin) : that.gremlin != null)
      return false;
    return bindings != null ? bindings.equals(that.bindings) : that.bindings == null;
  }

  @Override
  public int hashCode() {
    int result = gremlin != null ? gremlin.hashCode() : 0;
    result = 31 * result + (bindings != null ? bindings.hashCode() : 0);
    return result;
  }

  public String getGremlin() {
    return gremlin;
  }

  public void setGremlin(String gremlin) {
    this.gremlin = gremlin;
  }

  public Bindings getBindings() {
    return bindings;
  }

  public void setBindings(Bindings bindings) {
    this.bindings = bindings;
  }

  public String getRequestId() {
    return requestId;
  }

  public void setRequestId(String requestId) {
    this.requestId = requestId;
  }
}

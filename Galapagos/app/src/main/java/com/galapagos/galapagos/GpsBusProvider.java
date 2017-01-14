package com.galapagos.galapagos;

import com.squareup.otto.Bus;

public final class GpsBusProvider {
   private static final Bus BUS = new Bus();

   public static Bus getInstance() {
      return BUS;
   }

   // 생성사 X, 메서드를 통한 싱글톤
   private GpsBusProvider() {
   }
}
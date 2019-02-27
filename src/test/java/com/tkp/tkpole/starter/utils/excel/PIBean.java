package com.tkp.tkpole.starter.utils.excel;

import com.google.gson.annotations.Expose;
import com.tkp.tkpole.starter.utils.gson.TkpoleGsonBean;
import com.tkp.tkpole.starter.utils.parser.excel.rule.structure.annotation.ExcelBean;
import com.tkp.tkpole.starter.utils.parser.excel.rule.structure.annotation.ExcelRow;
import lombok.Data;

import java.util.List;

@TkpoleGsonBean
@ExcelBean(sheet = 1)
@Data
public class PIBean {
    @Expose
    @ExcelRow( rowBegin = 2,
            map = "A->order; B->state; C->name; D->relation; E->idType; F->idNo; G->birth; H->sex; I->plan; J->job; K->job2; L->jobDesc; M->tax")
    private List<RealPiBean> realPiBeanList;
}

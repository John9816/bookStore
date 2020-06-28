package com.neusoft.bookstore.picture.service;

import com.neusoft.bookstore.picture.model.Picture;
import com.neusoft.bookstore.util.ResponseVo;

/**
 * @author joy
 * @version 1.0
 * @date 2020/5/11 09:20
 */
public interface PictureService {

    ResponseVo addPic(Picture picture);

    ResponseVo listPic(Picture picture);

    ResponseVo updatePic(Picture picture);
}

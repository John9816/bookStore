package com.neusoft.bookstore.picture.mapper;

import com.neusoft.bookstore.picture.model.Picture;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author joy
 * @version 1.0
 * @date 2020/5/11 09:22
 */
@Mapper
public interface PictureMapper {

    int addPic(Picture picture);

    List<Picture> listPic(Picture picture);

    int updatePic(Picture picture);
}
